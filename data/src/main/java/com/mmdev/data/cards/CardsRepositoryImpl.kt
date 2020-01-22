/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 17:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.cards.CardItem
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.conversations.ConversationItem
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
                                              private val currentUserItem: UserItem):
		CardsRepository {

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
		private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val TAG = "mylogs_CardsRepoImpl"
	}

	private val currentUserInfo = currentUserItem.baseUserInfo

	/*
	* note: swiped left
	*/
	override fun addToSkipped(skippedCardItem: CardItem) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserInfo.city)
			.collection(currentUserInfo.gender)
			.document(currentUserInfo.userId)
			.collection(USER_SKIPPED_COLLECTION_REFERENCE)
			.document(skippedCardItem.baseUserInfo.userId)
			.set(skippedCardItem)
		Log.wtf(TAG, "skipped executed")
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
				.document(currentUserInfo.userId)
				.get()
				.addOnSuccessListener { userDoc ->
					if (userDoc.exists()) {

						emitter.onSuccess(true)

						handleMatch(likedCardItem, CardItem(currentUserInfo))

						val conversationId = firestore
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document()
							.id

						//set conversation for another user
						setupConversationForLikedUser(conversationId,
						                              likedCardItem.baseUserInfo)

						//set conversation for current user
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserInfo.city)
							.collection(currentUserInfo.gender)
							.document(currentUserInfo.userId)
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(conversationId)
							.set(ConversationItem(partner = likedCardItem.baseUserInfo,
							                      conversationId = conversationId,
							                      lastMessageTimestamp = null))

							.addOnSuccessListener { emitter.onSuccess(true) }
							.addOnFailureListener { emitter.onError(it) }

					}

					else {
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserInfo.city)
							.collection(currentUserInfo.gender)
							.document(currentUserInfo.userId)
							.collection(USER_LIKED_COLLECTION_REFERENCE)
							.document(likedCardItem.baseUserInfo.userId)
							.set(likedCardItem)
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
		val cardsRepositoryHelper = CardsRepositoryHelper(firestore, currentUserItem)
		return Single.zip(cardsRepositoryHelper.getAllUsersCards(),
		                  cardsRepositoryHelper.zipLists(),
		                  BiFunction<List<CardItem>, List<String>, List<CardItem>>
		                  { userList, ids  -> filterUsers(userList, ids) })
			.observeOn(Schedulers.io())

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
		deleteFromLikesCollection(likedCardItem.baseUserInfo, currentUserInfo.userId)

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
			.set(ConversationItem(partner = currentUserItem.baseUserInfo,
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
	private fun setLikedForBots(){
		firestore.collection(USERS_COLLECTION_REFERENCE).document("apzjzpbvdj").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("avzcixhglp").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("dtrfbjseuq").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("eoswtmcpul").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("ryknjtobrx").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("snykckkosz").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserInfo.userId).set(currentUserItem)
		Log.wtf(TAG, "liked for bots executed")
	}

}