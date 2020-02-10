/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.02.20 17:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.CardItem
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class CardsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              private val currentUser: UserItem,
                                              private val localStorageLists: LocalStorageLists):
		CardsRepository {

	private var currentUserDocReference: DocumentReference
	private val likedList: MutableList<String> = mutableListOf()
	private val matchedList: MutableList<String> = mutableListOf()
	private val skippedList: MutableList<String> = mutableListOf()

	init {
		currentUserDocReference = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
		likedList.addAll(localStorageLists.getLikedList())
		matchedList.addAll(localStorageLists.getMatchedList())
		skippedList.addAll(localStorageLists.getSkippedList())
	}

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
		private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"

		private const val USERS_FILTER_AGE = "baseUserInfo.age"

		private const val TAG = "mylogs_CardsRepoImpl"
	}


	/*
	* note: swiped left
	*/
	override fun addToSkipped(skippedCardItem: CardItem) {
		currentUserDocReference
			.collection(USER_SKIPPED_COLLECTION_REFERENCE)
			.document(skippedCardItem.baseUserInfo.userId)
			.set(skippedCardItem)
		skippedList.add(skippedCardItem.baseUserInfo.userId)
		localStorageLists.saveSkippedList(skippedList)
	}

	/*
	* note: swiped right
 	* check if users liked each other
	*/
	override fun checkMatch(likedCardItem: CardItem): Single<Boolean> {
		return Single.create(SingleOnSubscribe<Boolean> { emitter ->
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(likedCardItem.baseUserInfo.city)
				.collection(likedCardItem.baseUserInfo.gender)
				.document(likedCardItem.baseUserInfo.userId)
				.collection(USER_LIKED_COLLECTION_REFERENCE)
				.document(currentUser.baseUserInfo.userId)
				.get()
				.addOnSuccessListener { userDoc ->
					if (userDoc.exists()) {

						emitter.onSuccess(true)
						matchedList.add(likedCardItem.baseUserInfo.userId)
						localStorageLists.saveMatchedList(matchedList)

						val conversationId = firestore
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document()
							.id

						likedCardItem.conversationId = conversationId

						handleMatch(likedCardItem, CardItem(currentUser.baseUserInfo,
						                                    conversationId = conversationId))



						//set conversation for another user
						setupConversationForLikedUser(conversationId,
						                              likedCardItem.baseUserInfo)

						//set conversation for current user
						currentUserDocReference
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(conversationId)
							.set(ConversationItem(partner = likedCardItem.baseUserInfo,
							                      conversationId = conversationId,
							                      lastMessageTimestamp = null))

							.addOnSuccessListener { emitter.onSuccess(true) }
							.addOnFailureListener { emitter.onError(it) }

					}

					else {
						currentUserDocReference
							.collection(USER_LIKED_COLLECTION_REFERENCE)
							.document(likedCardItem.baseUserInfo.userId)
							.set(likedCardItem)
						likedList.add(likedCardItem.baseUserInfo.userId)
						localStorageLists.saveLikedList(likedList)
						emitter.onSuccess(false)
					}

			}.addOnFailureListener {
				Log.wtf(TAG, "check match fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	/* return filtered users list as Single */
	override fun getUsersByPreferences(): Single<List<CardItem>> {
		return Single.zip(getAllUsersCards(),
		                  zipLists(),
		                  BiFunction<List<CardItem>, List<String>, List<CardItem>>
		                  { userList, ids  -> filterUsers(userList, ids) })
			.subscribeOn(Schedulers.io())

	}

	/*
	* GET LIKED USERS IDS LIST
	*/
	override fun getLikedList(): Single<List<String>> {
		val query = currentUserDocReference
			.collection(USER_LIKED_COLLECTION_REFERENCE)
			.get()
		return Single.create(SingleOnSubscribe<List<String>>{ emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					likedList.clear()
					for (doc in it.result!!.documents){
						likedList.add(doc.id)
					}

					Log.wtf(TAG, "likes on complete, size = " + likedList.size)

					emitter.onSuccess(likedList)
					localStorageLists.saveLikedList(likedList)
				}
			}.addOnFailureListener {
				Log.wtf(TAG, "liked fail + $it")
				emitter.onError(it)
			}
		}).observeOn(Schedulers.io())

	}

	/*
	* GET MATCHED IDS LIST
	*/
	override fun getMatchedList(): Single<List<String>> {
		val query = currentUserDocReference
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.get()
		return Single.create(SingleOnSubscribe<List<String>> { emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					matchedList.clear()
					for (doc in it.result!!.documents){
						matchedList.add(doc.id)
					}

					Log.wtf(TAG, "matches on complete, size = " + matchedList.size)

					emitter.onSuccess(matchedList)
					//localStorageLists.saveMatchedList(matchedList)
				}
			}.addOnFailureListener {
				Log.wtf(TAG, "matches fail + $it")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	/*
	* GET SKIPPED USERS IDS LIST
	*/
//	override fun getSkippedList(): Single<List<String>> {
//		val query = currentUserDocReference
//			.collection(USER_SKIPPED_COLLECTION_REFERENCE)
//			.get()
//		return Single.create(SingleOnSubscribe<List<String>> { emitter ->
//			query.addOnCompleteListener {
//				if (it.result != null) {
//					for (doc in it.result!!.documents){
//						skippedList.add(doc.id)
//					}
//
//					//Log.wtf(TAG, "skips on complete, size = " + skippedUsersCardsIds.size)
//
//					emitter.onSuccess(skippedList)
//					localStorageLists.saveSkippedList(skippedList)
//				}
//			}.addOnFailureListener {
//				emitter.onError(it)
//				Log.wtf(TAG, "skipped fail + $it")
//			}
//
//		}).subscribeOn(Schedulers.io())
//
//	}

	/*
	* GET ALL USERS OBJECTS
	*/
	private fun getAllUsersCards(): Single<List<CardItem>> {
		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.preferredGender)
			.whereLessThanOrEqualTo(USERS_FILTER_AGE, currentUser.baseUserInfo.age)
			//.limit(limit)
			.get()
		return Single.create(SingleOnSubscribe<List<CardItem>>{ emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					val allUsersCards = ArrayList<CardItem>()
					for (doc in it.result!!.documents)
						allUsersCards.add(doc.toObject(CardItem::class.java)!!)
					//Log.wtf(TAG, "all on complete, size = " + allUsersCards.size)
					emitter.onSuccess(allUsersCards)
				}
			}.addOnFailureListener {
				Log.wtf(TAG, "all fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}


	/* return merged lists as Single */
	private fun zipLists(): Single<List<String>>{
		return Single.zip(getLikedList(),
		                  getMatchedList(),
		                  BiFunction<List<String>, List<String>, List<String>>
		                  { likes, matches -> mergeLists(likes, matches) })
			.subscribeOn(Schedulers.io())
	}

	/* merge all liked + matched + skipped users lists */
	private fun mergeLists(liked:List<String>,
	                       matched:List<String>,
	                       skipped: List<String> = skippedList): List<String>{
		val uidList = ArrayList<String>()
		uidList.addAll(liked)
		uidList.addAll(matched)
		uidList.addAll(skipped)
		return uidList
	}

	/**
	 * 1. add to matches collection for liked user
	 * 2. add to matches collection for CURRENT user
	 * 3. remove from likes collection for liked user
	 * 4. remove from likes collection for CURRENT user
	 */
	private fun handleMatch(likedCardItem: CardItem, currentCardItem: CardItem){
		addToMatchCollection(likedCardItem.baseUserInfo, currentCardItem)

		addToMatchCollection(currentCardItem.baseUserInfo, likedCardItem)

		//note:uncomment for release
		deleteFromLikesCollection(likedCardItem.baseUserInfo, currentUser.baseUserInfo.userId)

		deleteFromLikesCollection(currentCardItem.baseUserInfo,
		                          likedCardItem.baseUserInfo.userId)

		Log.wtf(TAG, "match handle executed")
	}

	private fun addToMatchCollection(userForWhichToAdd: BaseUserInfo, whomToAdd: CardItem){
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(userForWhichToAdd.city)
			.collection(userForWhichToAdd.gender)
			.document(userForWhichToAdd.userId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(whomToAdd.baseUserInfo.userId)
			.set(whomToAdd)

	}

	private fun deleteFromLikesCollection(userForWhichDelete: BaseUserInfo, whomToDeleteId: String){
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(userForWhichDelete.city)
			.collection(userForWhichDelete.gender)
			.document(userForWhichDelete.userId)
			.collection(USER_LIKED_COLLECTION_REFERENCE)
			.document(whomToDeleteId)
			.delete()
	}

	private fun setupConversationForLikedUser(conversationId: String, likedUser: BaseUserInfo){
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(likedUser.city)
			.collection(likedUser.gender)
			.document(likedUser.userId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
			.set(ConversationItem(partner = currentUser.baseUserInfo,
			                      conversationId = conversationId,
			                      lastMessageTimestamp = null))
	}

	/* return filtered all users list from already written ids as List<UserItem> */
	private fun filterUsers(cardItemList: List<CardItem>, ids: List<String>): List<CardItem>{
		val filteredUsersList = ArrayList<CardItem>()
		for (card in cardItemList)
			if (!ids.contains(card.baseUserInfo.userId))
				filteredUsersList.add(card)
		return filteredUsersList
	}


	//note:debug only
	//
//	private fun setLikedForBots(){
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("apzjzpbvdj").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("avzcixhglp").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("dtrfbjseuq").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("eoswtmcpul").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("ryknjtobrx").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("snykckkosz").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
//		Log.wtf(TAG, "liked for bots executed")
//	}

}