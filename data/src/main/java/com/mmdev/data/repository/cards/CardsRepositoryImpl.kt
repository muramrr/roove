/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.repository.cards

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryBounds
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.asSingle
import com.mmdev.data.core.firebase.deleteAsCompletable
import com.mmdev.data.core.firebase.setAsCompletable
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.core.log.logError
import com.mmdev.data.core.log.logWtf
import com.mmdev.domain.cards.CardsRepository
import com.mmdev.domain.conversations.ConversationItem
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.user.data.BaseUserInfo
import com.mmdev.domain.user.data.Gender
import com.mmdev.domain.user.data.Gender.*
import com.mmdev.domain.user.data.SelectionPreferences.PreferredGender.EVERYONE
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import javax.inject.Inject

/**
 * [CardsRepository] implementation
 */

class CardsRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore
): BaseRepository(), CardsRepository {
	
	companion object {
		private const val USERS_FILTER_GENDER = "baseUserInfo.gender"
		private const val USERS_FILTER_LOCATION_HASH = "location.hash"
 	}
	
	private val excludingIds: HashSet<String> = hashSetOf()
	
	private lateinit var cardsQueries: List<Query>
	private var cardsCursor: MutableList<DocumentSnapshot?> = mutableListOf()
	
	//init with default
	private var agesRange = IntRange(18, 24)
	
	private fun cardsQuery(user: UserItem): List<Query> = with(user.location) {
		//apply user ranges
		agesRange = IntRange(user.preferences.ageRange.minAge, user.preferences.ageRange.maxAge)
		
		excludingIds.add(user.baseUserInfo.userId)
		// Find cities within 50km of London
		val center = GeoLocation(latitude, longitude)
		val radiusInMeters = user.preferences.radius * 1000
		
		// Each item in 'bounds' represents a startAt/endAt pair. We have to issue
		// a separate query for each pair. There can be up to 9 pairs of bounds
		// depending on overlap, but in most cases there are 4.
		val bounds: List<GeoQueryBounds> = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeters)
		
		//map calculated bounds for 'ready to execute' queries
		bounds.map { bound ->
			logDebug(TAG, "Query for ranges: ${bound.startHash} ${bound.endHash}")
			
			fs.collection(USERS_COLLECTION)
				//people nearby
				.orderBy(USERS_FILTER_LOCATION_HASH)
				.startAt(bound.startHash)
				.endAt(bound.endHash)
				//filter by gender
				.whereIn(
					USERS_FILTER_GENDER,
					if (user.preferences.gender != EVERYONE) listOf(user.preferences.gender.name)
					else Gender.values().map { it.name }
				)
				.limit(20)
		}
		
	}
		
		
		
	/**
	 * if swiped left -> add skipped userId to skipped collection
	 */
	override fun skipUser(user: UserItem, skippedUser: UserItem): Completable =
		fs.collection(USERS_COLLECTION)
			.document(user.baseUserInfo.userId)
			.collection(USER_SKIPPED_COLLECTION)
			.document(skippedUser.baseUserInfo.userId)
			.setAsCompletable(mapOf(USER_ID_FIELD to skippedUser.baseUserInfo.userId))
			.also { excludingIds.add(skippedUser.baseUserInfo.userId) }
	
	/**
	 * Get users to show as swipeable cards
	 */
	override fun getUsersByPreferences(user: UserItem, initialLoading: Boolean): Single<List<UserItem>> =
		if (initialLoading) {
			//retrieve set of excluding ids
			getExcludedUserIds(user).zipWith(
				getUsersCardsByPreferences(user),
				BiFunction { excludingIds, users ->
					logDebug(TAG, "Initial cards query")
					return@BiFunction users.filterUsers(excludingIds)
				}
			).flatMap { filteredUsers ->
				logWtf(TAG, "Filtered users on initial: ${filteredUsers.size}")
				//if filtered users is null && cardsCursor contains at least one not null value
				if (filteredUsers.isEmpty() && cardsCursor.any { it != null }) {
					logDebug(TAG, "Filtered users is empty, but cursor contains non null, recursive call again")
					getUsersCardsByPreferences(user)
				}
				else {
					logDebug(TAG, "List is not empty or no profit for querying again")
					Single.just(filteredUsers)
				}
			}
		}
		//no need to retrieve set of excluding ids, we already has it initialized
		else {
			logDebug(TAG, "Requested not initial loading")
			getUsersCardsByPreferences(user).map {
				logDebug(TAG, "Not initial cards query, filtering..")
				it.filterUsers()
			}.flatMap { filteredUsers ->
				logDebug(TAG, "Filtered users: ${filteredUsers.size}")
				//if filtered users is null && cardsCursor contains at least one not null value
				if (filteredUsers.isEmpty() && cardsCursor.any { it != null }) {
					logDebug(TAG, "Filtered users is empty, but cursor contains non null, recursive call again")
					getUsersCardsByPreferences(user)
				}
				else {
					logDebug(TAG, "List is not empty or no profit for querying again")
					Single.just(filteredUsers)
				}
			}
		}.subscribeOn(MySchedulers.computation())
	
	/**
	 * GET USER CARDS BY PREFERENCES
	 * @see cardsQuery
	 */
	private fun getUsersCardsByPreferences(user: UserItem): Single<List<UserItem>> =
		SingleCreate<List<UserItem>> { emitter ->
			//apply start after cursor for query
			if (!this::cardsQueries.isInitialized) {
				cardsQueries = cardsQuery(user)
			}
			
			val queries = cardsQueries.mapIndexed { index, query ->
				//check if cursor initialized
				if (cardsCursor.isNotEmpty()) {
					logDebug(TAG, "Applying cards cursor for query at $index position")
					//and contains not null value -> apply startAfter cursor
					if (cardsCursor[index] != null) query.startAfter(cardsCursor[index]).get()
					//null tasks is not allowed, delete them
					else {
						cardsQueries.minus(query)
						cardsCursor.removeAt(index)
						null
					}
				}
				//else run standard initial query
				else query.get()
			}
			Tasks.whenAllComplete(queries.filterNotNull())
				.addOnSuccessListener { tasks ->
					emitter.onSuccess(
						tasks
							//get results
							.map { task -> task.result }
							.also { results ->
								
								//save last documents to apply them on next query call with startAfter
								cardsCursor = results.map { taskResult ->
									//for each snapshot check documents exists and save last
									with(taskResult as QuerySnapshot) {
										
										if (!documents.isNullOrEmpty()) documents.last()
										//else do not save anything item
										else null
									}
								} as MutableList<DocumentSnapshot?>
								logDebug(TAG, "New cursors: ${cardsCursor.map { it?.reference?.path }}")
								
							}
							//deserialize
							.map { taskResult ->
								(taskResult as QuerySnapshot?)?.toObjects(UserItem::class.java) ?: emptyList()
							}
							//flat list of lists into one list
							.flatten()
					)
				}
				.addOnFailureListener {
					logError(TAG, "$it")
					emitter.onError(it)
				}
		}.subscribeOn(MySchedulers.computation())
	
	
	private fun List<UserItem>.filterUsers(excluding: HashSet<String> = excludingIds): List<UserItem> =
		//filter from excludingIds
		filterNot {
			//logDebug(TAG, "Users retrieved by location and gender: $it")
			excluding.contains(it.baseUserInfo.userId)
		}
		//filter by age preferences
		.filter {
			//logDebug(TAG, "Filtered users from excluding set: $it")
			agesRange.contains(it.baseUserInfo.age)
		}
		
	
	
	
	/**
	 * execute getters and merge lists inside zip stream
	 */
	private fun getExcludedUserIds(user: UserItem): Single<HashSet<String>> =
		Single.zip(
			getUsersIdsInCollection(user, USER_LIKED_COLLECTION),
			getUsersIdsInCollection(user, USER_MATCHED_COLLECTION),
			getUsersIdsInCollection(user, USER_SKIPPED_COLLECTION),
			Function3 { likes: List<String>, matches: List<String>, skipped: List<String> ->
				excludingIds.addAll(likes)
				excludingIds.addAll(matches)
				excludingIds.addAll(skipped)
				return@Function3 excludingIds
			}
		).subscribeOn(MySchedulers.computation())
	
	/**
	 * GET USERS IDS LIST WHICH ARE LAYING INSIDE SPECIFIED [collection]
	 */
	private fun getUsersIdsInCollection(user: UserItem, collection: String): Single<List<String>> =
		fs.collection(USERS_COLLECTION)
			.document(user.baseUserInfo.userId)
			.collection(collection)
			.get()
			.asSingle()
			.map { query ->
				if (!query.isEmpty) {
					query.map { document -> document.id }
				}
				else {
					emptyList()
				}.plus(user.baseUserInfo.userId)
			}
	
	
	
	
	/**
	 * if swiped right -> check if there is match
	 * else -> add liked userId to liked collection
	 */
	override fun likeUserAndCheckMatch(user: UserItem, likedUserItem: UserItem): Single<Boolean> =
		fs.collection(USERS_COLLECTION)
			.document(likedUserItem.baseUserInfo.userId)
			.collection(USER_LIKED_COLLECTION)
			.document(user.baseUserInfo.userId)
			.get()
			.asSingle()
			.flatMap {
				val currentUserId = user.baseUserInfo.userId
				/**
				 * if your id exists in [likedUserItem] liked collection
				 * delete it from there and add both to matches collection
				 */
				if (it.exists()) {
					//create conversation for that pair
					val conversationId = fs
						.collection(CONVERSATIONS_COLLECTION)
						.document()
						.id
					
					Single.just(true)
						.doAfterSuccess {
							handleMatch(
								matchedUserItem = MatchedUserItem(
									likedUserItem.baseUserInfo,
									conversationId = conversationId
								),
								currentUserMatchedItem = MatchedUserItem(
									user.baseUserInfo,
									conversationId = conversationId
								)
							).subscribe { logDebug(TAG, "match handled") }
						}
					
				}
				
				/** if not exists -> add [likedUserItem] to your liked collection */
				else {
					fs.collection(USERS_COLLECTION)
						.document(currentUserId)
						.collection(USER_LIKED_COLLECTION)
						.document(likedUserItem.baseUserInfo.userId)
						.setAsCompletable(mapOf(USER_ID_FIELD to likedUserItem.baseUserInfo.userId))
						.toSingle { false } // no match
				}
			}
			.also { excludingIds.add(likedUserItem.baseUserInfo.userId) }
	
	/**
	 * 1. add to matches collection for liked user
	 * 2. add to matches collection for CURRENT user
	 * 3. remove from likes collection for liked user
	 * 4. remove from likes collection for CURRENT user
	 */
	private fun handleMatch(
		matchedUserItem: MatchedUserItem,
		currentUserMatchedItem: MatchedUserItem
	): Completable = Completable.concatArray(
		// add current user to matched user MATCHED collection
		addToMatchCollection(
			userForWhichToAdd = matchedUserItem,
			whomToAdd = currentUserMatchedItem
		),
		// delete current user from matched user LIKED collection
		fs.collection(USERS_COLLECTION)
			.document(matchedUserItem.baseUserInfo.userId)
			.collection(USER_LIKED_COLLECTION)
			.document(currentUserMatchedItem.baseUserInfo.userId)
			.deleteAsCompletable(),
		//set conversation for matched user
		setConversation(matchedUserItem.baseUserInfo, currentUserMatchedItem),
		
		// no need to delete from LIKED collection for current user, because we just liked and
		// this match checking is in process, which based on liked user collections data
		addToMatchCollection(
			userForWhichToAdd = currentUserMatchedItem,
			whomToAdd = matchedUserItem
		).andThen(
			//set conversation for current user
			setConversation(currentUserMatchedItem.baseUserInfo, matchedUserItem),
		)
		
	).subscribeOn(MySchedulers.io())
	
	
	private fun addToMatchCollection(
		userForWhichToAdd: MatchedUserItem,
		whomToAdd: MatchedUserItem
	) = fs.collection(USERS_COLLECTION)
		.document(userForWhichToAdd.baseUserInfo.userId)
		.collection(USER_MATCHED_COLLECTION)
		.document(whomToAdd.baseUserInfo.userId)
		.setAsCompletable(whomToAdd)
	
	private fun setConversation(
		userForWhichToAdd: BaseUserInfo,
		whomToAdd: MatchedUserItem
	) = fs.collection(USERS_COLLECTION)
		.document(userForWhichToAdd.userId)
		.collection(CONVERSATIONS_COLLECTION)
		.document(whomToAdd.conversationId)
		.setAsCompletable(
			ConversationItem(
				partner = whomToAdd.baseUserInfo,
				conversationId = whomToAdd.conversationId,
				lastMessageTimestamp = null
			)
		)

}