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
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.asSingle
import com.mmdev.data.core.firebase.deleteAsCompletable
import com.mmdev.data.core.firebase.setAsCompletable
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.core.log.logError
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
		private const val cardsLimit: Long = 20
 	}
	
	private val excludingIds: HashSet<String> = hashSetOf()
	
	private fun cardsQuery(user: UserItem) = with(user.location) {
		excludingIds.add(user.baseUserInfo.userId)
		// Find cities within 50km of London
		val center = GeoLocation(latitude, longitude)
		val radiusInMeters = user.preferences.radius * 1000
		
		// Each item in 'bounds' represents a startAt/endAt pair. We have to issue
		// a separate query for each pair. There can be up to 9 pairs of bounds
		// depending on overlap, but in most cases there are 4.
		val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInMeters)
		
		//map calculated bounds for 'ready to execute' queries
		bounds.map { bound ->
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
				.get()
			//todo: is limit needed? or how to implement pagination?
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
			getExcludedUserIds(user).zipWith(
				getUsersCardsByPreferences(user),
				BiFunction { excludingIds, users ->
					return@BiFunction users
						//filter from excludingIds
						.filterNot {
							//logDebug(TAG, "Users retrieved by location and gender: $it")
							excludingIds.contains(it.baseUserInfo.userId)
						}
						//filter by age preferences
						.filter {
							//logDebug(TAG, "Filtered users from excluding set: $it")
							IntRange(
								user.preferences.ageRange.minAge,
								user.preferences.ageRange.maxAge
							).contains(it.baseUserInfo.age)
						}
				}
			)
		}
		else { getUsersCardsByPreferences(user) }
			.subscribeOn(MySchedulers.computation())
	
	/**
	 * GET USER CARDS BY PREFERENCES
	 * @see cardsQuery
	 */
	private fun getUsersCardsByPreferences(user: UserItem): Single<List<UserItem>> =
		SingleCreate<List<UserItem>> { emitter ->
			val queries = cardsQuery(user)
			Tasks.whenAllComplete(queries)
				.addOnSuccessListener { tasks ->
					emitter.onSuccess(
						tasks
							.map { task -> task.result }
							.map { taskResult -> (taskResult as QuerySnapshot?)?.toObjects(UserItem::class.java) ?: emptyList() }
							.flatten()
					)
				}
				.addOnFailureListener {
					logError(TAG, "$it")
					emitter.onError(it)
				}
		}.subscribeOn(MySchedulers.computation())
	
	private fun zipQueryResults() {}
	
	
	/**
	 * execute getters and merge lists inside zip stream
	 */
	private fun getExcludedUserIds(user: UserItem): Single<List<String>> =
		Single.zip(
			getUsersIdsInCollection(user, USER_LIKED_COLLECTION),
			getUsersIdsInCollection(user, USER_MATCHED_COLLECTION),
			getUsersIdsInCollection(user, USER_SKIPPED_COLLECTION),
			Function3 { likes: List<String>, matches: List<String>, skipped: List<String> ->
				val combinedList = listOf(likes, matches, skipped).flatten()
				excludingIds.addAll(combinedList)
				return@Function3 combinedList
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