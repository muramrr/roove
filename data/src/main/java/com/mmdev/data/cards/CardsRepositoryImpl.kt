/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.03.20 17:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.user.UserWrapper
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class CardsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              userWrapper: UserWrapper):
		CardsRepository, BaseRepositoryImpl(firestore, userWrapper) {

	private val likedList = mutableListOf<String>()
	private val matchedList = mutableListOf<String>()
	private val skippedList = mutableListOf<String>()

	private var specifiedCardsQuery: Query = firestore.collection(USERS_COLLECTION_REFERENCE)
		.document(currentUser.baseUserInfo.city)
		.collection(currentUser.baseUserInfo.preferredGender)
		.orderBy(USERS_FILTER_AGE)
		.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.minAge)
		.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.maxAge)
		.orderBy(USERS_FILTER_ID, Query.Direction.DESCENDING)

	private val cardsLimit = 19
	private var filteredDocuments: MutableSet<QueryDocumentSnapshot>? = null
	private lateinit var maleCardsQuery: Query
	private lateinit var femaleCardsQuery: Query
	private var filteredMaleDocuments: MutableSet<QueryDocumentSnapshot>? = null
	private var filteredFemaleDocuments: MutableSet<QueryDocumentSnapshot>? = null

	companion object {
		private const val USERS_FILTER_ID = "baseUserInfo.userId"
		private const val USERS_FILTER_AGE = "baseUserInfo.age"
		private const val MALE_COLLECTION_PATH = "male"
		private const val FEMALE_COLLECTION_PATH = "female"
	}


	/*
	* if swiped left -> add skipped userId to skipped collection
	*/
	override fun addToSkipped(skippedUserItem: UserItem) {
		currentUserDocRef
			.collection(USER_SKIPPED_COLLECTION_REFERENCE)
			.document(skippedUserItem.baseUserInfo.userId)
			.set(mapOf(USER_ID_FIELD to skippedUserItem.baseUserInfo.userId))

		skippedList.add(skippedUserItem.baseUserInfo.userId)
		filteredDocuments = filteredDocuments?.filter {
			doc -> doc.id != skippedUserItem.baseUserInfo.userId
		}?.toMutableSet()
	}

	/*
	* if swiped right -> check if there is match
	* else -> add liked userId to liked collection
	*/
	override fun checkMatch(likedUserItem: UserItem): Single<Boolean> {
		return Single.create(SingleOnSubscribe<Boolean> { emitter ->
			val likedUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(likedUserItem.baseUserInfo.city)
				.collection(likedUserItem.baseUserInfo.gender)
				.document(likedUserItem.baseUserInfo.userId)

			likedUserDocRef
				.collection(USER_LIKED_COLLECTION_REFERENCE)
				.document(currentUser.baseUserInfo.userId)
				.get()
				.addOnSuccessListener { userDoc ->
					//if true - user you've liked likes you too
					//else - add to like collection
					if (userDoc.exists()) {

						emitter.onSuccess(true)
						matchedList.add(likedUserItem.baseUserInfo.userId)
						filteredDocuments = filteredDocuments?.filter {
							doc -> doc.id != likedUserItem.baseUserInfo.userId
						}?.toMutableSet()

						//create predefined conversation for this match
						val conversationId = firestore
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document()
							.id

						//execute add and remove documents for each of users
						handleMatch(MatchedUserItem(likedUserItem.baseUserInfo, conversationId = conversationId),
						            MatchedUserItem(currentUser.baseUserInfo, conversationId = conversationId))

						//set conversation for liked user
						likedUserDocRef
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(conversationId)
							.set(ConversationItem(partner = currentUser.baseUserInfo,
							                      conversationId = conversationId,
							                      lastMessageTimestamp = null))

						//set conversation for current user
						currentUserDocRef
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(conversationId)
							.set(ConversationItem(partner = likedUserItem.baseUserInfo,
							                      conversationId = conversationId,
							                      lastMessageTimestamp = null))

							.addOnSuccessListener { emitter.onSuccess(true) }
							.addOnFailureListener { emitter.onError(it) }

					}
					else {
						currentUserDocRef
							.collection(USER_LIKED_COLLECTION_REFERENCE)
							.document(likedUserItem.baseUserInfo.userId)
							.set(mapOf(USER_ID_FIELD to likedUserItem.baseUserInfo.userId))

						likedList.add(likedUserItem.baseUserInfo.userId)
						filteredDocuments = filteredDocuments?.filter {
							doc -> doc.id != likedUserItem.baseUserInfo.userId
						}?.toMutableSet()
						emitter.onSuccess(false)
					}

			}.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}

	/* return filtered users list as Single */
	override fun getUsersByPreferences(): Single<List<UserItem>> {
		reInit()
		return zipLists()
			.flatMap { getUsersCardsByPreferences(it) }
			.subscribeOn(Schedulers.computation())
	}

	private fun parseFilteredUsers(setOfUsers: MutableSet<QueryDocumentSnapshot>): List<UserItem>{
		return if (setOfUsers.isNotEmpty()) {
			val resultList = mutableListOf<UserItem>()
			var count = 0
			for (doc in setOfUsers) {
				if (count < cardsLimit) {
					resultList.add(doc.toObject(UserItem::class.java))
					count++
				}
			}
			resultList.shuffled()
		} else emptyList()
	}

	/*
	* GET USER CARDS BY PREFERENCES RELATED TO PREFERREDGENDER
	*/
	private fun getUsersCardsByPreferences(excludingIds: List<String>): Single<List<UserItem>> {
		return if (currentUser.baseUserInfo.preferredGender == "everyone")
			Single.zip(getAllFemaleUsersCards(excludingIds),
			           getAllMaleUsersCards(excludingIds),
			           BiFunction<List<UserItem>, List<UserItem>, List<UserItem>>
			           { female, male -> return@BiFunction listOf(female, male).flatten() })
				.subscribeOn(Schedulers.computation())
		else Single.create(SingleOnSubscribe<List<UserItem>>{ emitter ->
			specifiedCardsQuery
				.get()
				.addOnSuccessListener { documents ->
					if (!documents.isEmpty) {
						//filter users which is not in excluding Ids list
						if (filteredDocuments == null) {
							filteredDocuments = documents.filter {
								!excludingIds.contains(it.id) && it.id != currentUserId
							}.toMutableSet()
							val specifiedResultList = parseFilteredUsers(filteredDocuments!!)
							emitter.onSuccess(specifiedResultList)
						}
						else emitter.onSuccess(parseFilteredUsers(filteredDocuments!!))

					} else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.computation())
	}

	private fun getAllMaleUsersCards(excludingIds: List<String>): Single<List<UserItem>> {
		maleCardsQuery = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(MALE_COLLECTION_PATH)
			.orderBy(USERS_FILTER_AGE)
			.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.minAge)
			.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.maxAge)
			.orderBy(USERS_FILTER_ID, Query.Direction.DESCENDING)

		return Single.create(SingleOnSubscribe<List<UserItem>>{ emitter ->
			maleCardsQuery
				.get()
				.addOnSuccessListener { documents ->
					if (!documents.isEmpty) {
						//filter users which is not in excluding Ids list
						if (filteredMaleDocuments == null) {
							filteredMaleDocuments = documents.filter {
								!excludingIds.contains(it.id) && it.id != currentUserId
							}.toMutableSet()
							val maleResultList = parseFilteredUsers(filteredMaleDocuments!!)
							emitter.onSuccess(maleResultList)
							Log.wtf(TAG, "filtered null male = ${maleResultList.size}")
						}
						else {
							val maleResultList = parseFilteredUsers(filteredMaleDocuments!!)
							emitter.onSuccess(maleResultList)
							Log.wtf(TAG, "filtered not null male = ${maleResultList.size}")
						}

					} else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}

	private fun getAllFemaleUsersCards(excludingIds: List<String>): Single<List<UserItem>> {
		femaleCardsQuery = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(FEMALE_COLLECTION_PATH)
			.orderBy(USERS_FILTER_AGE)
			.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.minAge)
			.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.maxAge)
			.orderBy(USERS_FILTER_ID, Query.Direction.DESCENDING)

		return Single.create(SingleOnSubscribe<List<UserItem>>{ emitter ->
			femaleCardsQuery
				.get()
				.addOnSuccessListener { documents ->
					if (!documents.isEmpty) {
						//filter users which is not in excluding Ids list
						if (filteredFemaleDocuments == null) {
							filteredFemaleDocuments = documents.filter {
								!excludingIds.contains(it.id) && it.id != currentUserId
							}.toMutableSet()
							val femaleResultList = parseFilteredUsers(filteredFemaleDocuments!!)
							emitter.onSuccess(femaleResultList)
							Log.wtf(TAG, "filtered null female = ${femaleResultList.size}")
						}
						else {
							val femaleResultList = parseFilteredUsers(filteredFemaleDocuments!!)
							emitter.onSuccess(femaleResultList)
							Log.wtf(TAG, "filtered not null female = ${femaleResultList.size}")
						}

					} else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}


	/* execute getters and merge lists inside zip stream */
	private fun zipLists(): Single<List<String>> =
		Single.zip(getLikedList(),
		           getMatchedList(),
		           getSkippedList(),
		           Function3<List<String>, List<String>, List<String>, List<String>>
		           { likes, matches, skipped -> mergeLists(likes, matches, skipped) })
			.subscribeOn(Schedulers.computation())

	/* merge 3 lists of type <T> */
	private fun <T> mergeLists(t1: List<T> = emptyList(),
	                           t2: List<T> = emptyList(),
	                           t3: List<T> = emptyList()): List<T> = listOf(t1, t2, t3).flatten()

	/*
	* GET LIKED USERS IDS LIST
	*/
	private fun getLikedList(): Single<List<String>> =
		Single.create(SingleOnSubscribe<List<String>>{ emitter ->
			currentUserDocRef
				.collection(USER_LIKED_COLLECTION_REFERENCE)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						for (doc in it){
							if (!likedList.contains(doc.id))
								likedList.add(doc.id)
						}
						emitter.onSuccess(likedList)
					}
					else emitter.onSuccess(likedList)
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())

	/*
	* GET MATCHED IDS LIST
	*/
	private fun getMatchedList(): Single<List<String>> =
		Single.create(SingleOnSubscribe<List<String>> { emitter ->
			currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						for (doc in it){
							if (!matchedList.contains(doc.id))
								matchedList.add(doc.id)
						}
						emitter.onSuccess(matchedList)
					}
					else emitter.onSuccess(matchedList) }
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())

	/*
	* GET SKIPPED USERS IDS LIST
	*/
	private fun getSkippedList(): Single<List<String>> =
		Single.create(SingleOnSubscribe<List<String>> { emitter ->
			currentUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						for (doc in it) {
							if (!skippedList.contains(doc.id))
								skippedList.add(doc.id)
						}
						emitter.onSuccess(skippedList)
					}
					else emitter.onSuccess(skippedList)
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())


	/**
	 * 1. add to matches collection for liked user
	 * 2. add to matches collection for CURRENT user
	 * 3. remove from likes collection for liked user
	 * 4. remove from likes collection for CURRENT user
	 */
	private fun handleMatch(matchedUserItem: MatchedUserItem, currentUserMatchedItem: MatchedUserItem) {
		addToMatchCollection(userForWhichToAdd = matchedUserItem.baseUserInfo,
		                     whomToAdd = currentUserMatchedItem)

		addToMatchCollection(userForWhichToAdd = currentUserMatchedItem.baseUserInfo,
		                     whomToAdd = matchedUserItem)

		//note:uncomment for release
		deleteFromLikesCollection(userForWhichDelete = matchedUserItem.baseUserInfo,
		                          whomToDeleteId = currentUser.baseUserInfo.userId)

		deleteFromLikesCollection(userForWhichDelete = currentUserMatchedItem.baseUserInfo,
		                          whomToDeleteId = matchedUserItem.baseUserInfo.userId)

		Log.wtf(TAG, "match handle executed")
	}

	private fun addToMatchCollection(userForWhichToAdd: BaseUserInfo, whomToAdd: MatchedUserItem) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(userForWhichToAdd.city)
			.collection(userForWhichToAdd.gender)
			.document(userForWhichToAdd.userId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(whomToAdd.baseUserInfo.userId)
			.set(whomToAdd)

	}

	private fun deleteFromLikesCollection(userForWhichDelete: BaseUserInfo, whomToDeleteId: String) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(userForWhichDelete.city)
			.collection(userForWhichDelete.gender)
			.document(userForWhichDelete.userId)
			.collection(USER_LIKED_COLLECTION_REFERENCE)
			.document(whomToDeleteId)
			.delete()
	}

	override fun reInit() {
		super.reInit()
		likedList.clear()
		skippedList.clear()
		matchedList.clear()
		filteredDocuments = null
		filteredFemaleDocuments = null
		filteredMaleDocuments = null
		specifiedCardsQuery = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.preferredGender)
			.orderBy(USERS_FILTER_AGE)
			.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.minAge)
			.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.maxAge)
			.orderBy(USERS_FILTER_ID, Query.Direction.DESCENDING)
	}


}