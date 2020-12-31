/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 17:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.repository.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.mmdev.business.cards.CardsRepository
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.ExecuteSchedulers
import com.mmdev.data.repository.user.UserWrapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class CardsRepositoryImpl @Inject constructor(
	private val firestore: FirebaseFirestore,
	userWrapper: UserWrapper
):
        CardsRepository, BaseRepositoryImpl(firestore, userWrapper) {

	private var specifiedCardsQuery: Query = firestore.collection(USERS_COLLECTION_REFERENCE)
		.document(currentUser.baseUserInfo.city)
		.collection(currentUser.baseUserInfo.preferredGender)
		.orderBy(USERS_FILTER_AGE)
		.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.minAge)
		.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.maxAge)
		.orderBy(USERS_FILTER_ID, Query.Direction.DESCENDING)

	private val cardsLimit = 19
	private val excludingIds = mutableListOf<String>()
	private var filteredDocuments: MutableSet<QueryDocumentSnapshot>? = null
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
	override fun addToSkipped(skippedUserItem: UserItem): Completable =
		CompletableCreate { emitter ->
			addToExcludingIds(skippedUserItem.baseUserInfo.userId)
			filteredDocuments?.filter {
				doc -> doc.id != skippedUserItem.baseUserInfo.userId
			}
			currentUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(skippedUserItem.baseUserInfo.userId)
				.set(mapOf(USER_ID_FIELD to skippedUserItem.baseUserInfo.userId))
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }
		}


	/*
	* if swiped right -> check if there is match
	* else -> add liked userId to liked collection
	*/
	override fun checkMatch(likedUserItem: UserItem): Single<Boolean> {
		addToExcludingIds(likedUserItem.baseUserInfo.userId)
		filteredDocuments?.filter {
			doc -> doc.id != likedUserItem.baseUserInfo.userId
		}
		return SingleCreate<Boolean> { emitter ->
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
						//create predefined conversation for this match
						val conversationId = firestore
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document()
							.id

						//execute add and remove documents for each of users
						handleMatch(MatchedUserItem(likedUserItem.baseUserInfo, conversationId = conversationId),
						            MatchedUserItem(currentUser.baseUserInfo, conversationId = conversationId))


						likedUserDocRef
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(conversationId)
							.set(
                                ConversationItem(partner = currentUser.baseUserInfo,
                                                 conversationId = conversationId,
                                                 lastMessageTimestamp = null)
                            )
							.addOnFailureListener { emitter.onError(it) }

						//set conversation for current user
						currentUserDocRef
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(conversationId)
							.set(
                                ConversationItem(partner = likedUserItem.baseUserInfo,
                                                 conversationId = conversationId,
                                                 lastMessageTimestamp = null)
                            )
							.addOnFailureListener { emitter.onError(it) }

					}
					else {
						currentUserDocRef
							.collection(USER_LIKED_COLLECTION_REFERENCE)
							.document(likedUserItem.baseUserInfo.userId)
							.set(mapOf(USER_ID_FIELD to likedUserItem.baseUserInfo.userId))
							.addOnSuccessListener { emitter.onSuccess(false) }
							.addOnFailureListener { emitter.onError(it) }

					}
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())
	}

	/* return filtered users list as Single */
	override fun getUsersByPreferences(initialLoading: Boolean): Single<List<UserItem>> {
		return if (initialLoading){
			reInit()
			zipLists().flatMap { getUsersCardsByPreferences(it) }.subscribeOn(ExecuteSchedulers.computation())
		}
		else {
			getUsersCardsByPreferences(excludingIds)
				.subscribeOn(ExecuteSchedulers.computation())

		}
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
		this.excludingIds.addAll(excludingIds)
		return if (currentUser.baseUserInfo.preferredGender == "everyone")
			Single.zip(getAllFemaleUsersCards(excludingIds),
			           getAllMaleUsersCards(excludingIds),
			           BiFunction {
							   female: List<UserItem>,
							   male: List<UserItem> -> return@BiFunction listOf(female, male).flatten()
			           })
				.subscribeOn(ExecuteSchedulers.computation())

		else SingleCreate<List<UserItem>> { emitter ->
			specifiedCardsQuery
				.get()
				.addOnSuccessListener { documents ->
					if (!documents.isEmpty) {
						//filter users which is not in excluding Ids list
						filteredDocuments = documents.filter {
							!excludingIds.contains(it.id) && it.id != currentUserId
						}.toMutableSet()

						val specifiedResultList = parseFilteredUsers(filteredDocuments!!)
						emitter.onSuccess(specifiedResultList)

					} else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.computation())
	}

	private fun getAllMaleUsersCards(excludingIds: List<String>): Single<List<UserItem>> {
		return SingleCreate<List<UserItem>> { emitter ->
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUser.baseUserInfo.city)
				.collection(MALE_COLLECTION_PATH)
				.orderBy(USERS_FILTER_AGE)
				.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.minAge)
				.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.maxAge)
				.orderBy(USERS_FILTER_ID, Query.Direction.DESCENDING)
				.get()
				.addOnSuccessListener { documents ->
					if (!documents.isEmpty) {
						filteredMaleDocuments = documents.filter {
							!excludingIds.contains(it.id) && it.id != currentUserId
						}.toMutableSet()
						val maleResultList = parseFilteredUsers(filteredMaleDocuments!!)
						emitter.onSuccess(maleResultList)

					} else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())
	}

	private fun getAllFemaleUsersCards(excludingIds: List<String>): Single<List<UserItem>> {
		return SingleCreate<List<UserItem>> { emitter ->
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUser.baseUserInfo.city)
				.collection(FEMALE_COLLECTION_PATH)
				.orderBy(USERS_FILTER_AGE)
				.whereGreaterThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.minAge)
				.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.preferredAgeRange.maxAge)
				.orderBy(USERS_FILTER_ID, Query.Direction.DESCENDING)
				.get()
				.addOnSuccessListener { documents ->
					if (!documents.isEmpty) {
						//filter users which is not in excluding Ids list
						filteredFemaleDocuments = documents.filter {
							!excludingIds.contains(it.id) && it.id != currentUserId
						}.toMutableSet()
						val femaleResultList = parseFilteredUsers(filteredFemaleDocuments!!)
						emitter.onSuccess(femaleResultList)
					} else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())
	}


	/* execute getters and merge lists inside zip stream */
	private fun zipLists(): Single<List<String>> =
		Single.zip(getLikedList(),
		           getMatchedList(),
		           getSkippedList(),
		           Function3 {
			           likes: List<String>,
			           matches: List<String>,
			           skipped: List<String> -> mergeLists(likes, matches, skipped)
		           }
		).subscribeOn(ExecuteSchedulers.computation())

	/* merge 3 lists of type <T> */
	private fun <T> mergeLists(t1: List<T> = emptyList(),
	                           t2: List<T> = emptyList(),
	                           t3: List<T> = emptyList()): List<T> = listOf(t1, t2, t3).flatten()

	/*
	* GET LIKED USERS IDS LIST
	*/
	private fun getLikedList(): Single<List<String>> =
		SingleCreate<List<String>> { emitter ->
			currentUserDocRef
				.collection(USER_LIKED_COLLECTION_REFERENCE)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val likedList = mutableListOf<String>()
						likedList.addAll(it.map { doc -> doc.id })
						emitter.onSuccess(likedList)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())

	/*
	* GET MATCHED IDS LIST
	*/
	private fun getMatchedList(): Single<List<String>> =
		SingleCreate<List<String>> { emitter ->
			currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val matchedList = mutableListOf<String>()
						matchedList.addAll(it.map { doc -> doc.id })
						emitter.onSuccess(matchedList)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())

	/*
	* GET SKIPPED USERS IDS LIST
	*/
	private fun getSkippedList(): Single<List<String>> =
		SingleCreate<List<String>> { emitter ->
			currentUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val skippedList = mutableListOf<String>()
						skippedList.addAll(it.map { doc -> doc.id })
						emitter.onSuccess(skippedList)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())


	private fun addToExcludingIds(id: String) {
		if (!excludingIds.contains(id))
			excludingIds.add(id)
	}

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
		excludingIds.clear()
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