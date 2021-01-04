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

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.asSingle
import com.mmdev.data.core.firebase.executeAndDeserializeSingle
import com.mmdev.data.core.firebase.setAsCompletable
import com.mmdev.data.core.log.logDebug
import com.mmdev.domain.cards.CardsRepository
import com.mmdev.domain.conversations.ConversationItem
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.user.data.BaseUserInfo
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import javax.inject.Inject

/**
 * [CardsRepository] implementation
 */

class CardsRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore
): BaseRepository(), CardsRepository {
	
	companion object {
		private const val USERS_FILTER_GENDER = "baseUserInfo.gender"
		private const val USERS_FILTER_AGE = "baseUserInfo.age"
		private const val USERS_FILTER_LOCATION_LAT = "location.latitude"
		private const val USERS_FILTER_LOCATION_LON = "location.longitude"
 	}
	
	private val cardsLimit: Long = 19
	private val excludingIds: MutableSet<String> = mutableSetOf()
	
	private fun cardsQuery(user: UserItem): Query = with(user.location.getBounds(user.preferences.radius)) {
		fs.collection(USERS_COLLECTION)
			//in preferred radius around user location
			.whereGreaterThanOrEqualTo(USERS_FILTER_LOCATION_LAT, minPoint.latitude)
			.whereLessThanOrEqualTo(USERS_FILTER_LOCATION_LAT, maxPoint.latitude)
			.whereGreaterThanOrEqualTo(USERS_FILTER_LOCATION_LON, minPoint.longitude)
			.whereLessThanOrEqualTo(USERS_FILTER_LOCATION_LON, maxPoint.longitude)
			//filter by preferred age
			.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, user.preferences.ageRange.minAge)
			.whereLessThanOrEqualTo(USERS_FILTER_AGE, user.preferences.ageRange.maxAge)
			//only preferred gender, if everyone -> include both genders
			//todo filter by gender
//			.whereIn(
//				USERS_FILTER_GENDER,
//				if (user.baseUserInfo.preferredGender != "everyone")
//					listOf(user.baseUserInfo.preferredGender)
//				else listOf("male", "female")
//			)
			//filtered from excluded
			.whereNotIn(USER_ID_FIELD, excludingIds.toList())
			.limit(cardsLimit)
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
			getExcludedUserIds(user)
				.flatMap {
					getUsersCardsByPreferences(user)
				}
		}
		else { getUsersCardsByPreferences(user) }
			.subscribeOn(MySchedulers.computation())
	
	/**
	 * GET USER CARDS BY PREFERENCES
	 * @see cardsQuery
	 */
	private fun getUsersCardsByPreferences(user: UserItem): Single<List<UserItem>> =
		cardsQuery(user)
			.executeAndDeserializeSingle(UserItem::class.java)
	
	
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
					
					handleMatch(
						matchedUserItem = MatchedUserItem(
							likedUserItem.baseUserInfo,
							conversationId = conversationId
						),
						currentUserMatchedItem = MatchedUserItem(
							user.baseUserInfo,
							conversationId = conversationId
						)
					).map { true } // match exists
				}
				
				/** if not exists -> add [likedUserItem] to your liked collection */
				else {
					fs.collection(USERS_COLLECTION)
						.document(currentUserId)
						.collection(USER_LIKED_COLLECTION)
						.document(likedUserItem.baseUserInfo.userId)
						.set(mapOf(USER_ID_FIELD to likedUserItem.baseUserInfo.userId))
						.asSingle()
						.map { false } // no match
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
	): Single<Unit> {
		
		//operations to manipulate collections for matched user
		val matchedUserCollections = Single.zip(
			// add current user to matched user MATCHED collection
			addToMatchCollection(
				userForWhichToAdd = matchedUserItem.baseUserInfo,
				whomToAdd = currentUserMatchedItem.baseUserInfo
			),
			// delete current user from matched user LIKED collection
			fs.collection(USERS_COLLECTION)
				.document(matchedUserItem.baseUserInfo.userId)
				.collection(USER_LIKED_COLLECTION)
				.document(currentUserMatchedItem.baseUserInfo.userId)
				.delete()
				.asSingle(),
			//set conversation for matched user
			setConversation(matchedUserItem.baseUserInfo, currentUserMatchedItem),
			Function3 { t1, t2, t3 ->
				return@Function3
			}
		).subscribeOn(MySchedulers.io())
		
		// no need to delete from LIKED collection for current user, because we just liked and
		// this match checking is in process, which based on liked user collections data
		val currentUserCollections = addToMatchCollection(
			userForWhichToAdd = currentUserMatchedItem.baseUserInfo,
			whomToAdd = matchedUserItem.baseUserInfo
		).zipWith(
			//set conversation for current user
			setConversation(currentUserMatchedItem.baseUserInfo, matchedUserItem),
			BiFunction { t1, t2 -> return@BiFunction }
		
		).subscribeOn(MySchedulers.io())
		
		//combine
		return matchedUserCollections.zipWith(
			currentUserCollections,
			BiFunction { t1, t2 ->
				logDebug(TAG, "match handle executed")
				return@BiFunction
			}
		).subscribeOn(MySchedulers.io())
		
	}
	
	private fun addToMatchCollection(
		userForWhichToAdd: BaseUserInfo,
		whomToAdd: BaseUserInfo
	) = fs.collection(USERS_COLLECTION)
		.document(userForWhichToAdd.userId)
		.collection(USER_MATCHED_COLLECTION)
		.document(whomToAdd.userId)
		.set(whomToAdd)
		.asSingle()
	
	private fun setConversation(
		userForWhichToAdd: BaseUserInfo,
		whomToAdd: MatchedUserItem
	) = fs.collection(USERS_COLLECTION)
		.document(userForWhichToAdd.userId)
		.collection(CONVERSATIONS_COLLECTION)
		.document(whomToAdd.conversationId)
		.set(
			ConversationItem(
				partner = whomToAdd.baseUserInfo,
				conversationId = whomToAdd.conversationId,
				lastMessageTimestamp = null
			)
		)
		.asSingle()

}