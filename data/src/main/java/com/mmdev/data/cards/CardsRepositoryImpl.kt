/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.user.model.UserItem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class CardsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              private val currentUserItem: UserItem): CardsRepository {

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
		private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val TAG = "mylogs"
	}

	private val currentUserId = currentUserItem.userId

	/*
	* note: swiped left
	*/
	override fun addToSkipped(skippedCardItem: CardItem) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_SKIPPED_COLLECTION_REFERENCE)
			.document(skippedCardItem.userId)
			.set(skippedCardItem)
		Log.wtf(TAG, "skipped executed")
	}

	/*
	* note: swiped right
 	* check if users liked each other
	*/
	override fun checkMatch(likedCardItem: CardItem): Single<Boolean> {

		val currentCardItem = convertUserItemToCardItem(currentUserItem)

		return Single.create(SingleOnSubscribe<Boolean> { emitter ->

			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(likedCardItem.userId)
				.collection(USER_LIKED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.get()
				.addOnSuccessListener { documentSnapshot ->
					if (documentSnapshot.exists()) {

						emitter.onSuccess(true)

						handleMatch(likedCardItem, currentCardItem)

						val conversationId = firestore
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document()
							.id

						//set conversation for another user
						setupConversationForUser(conversationId, likedCardItem.userId)

						//set conversation for current user
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(CONVERSATIONS_COLLECTION_REFERENCE)
							.document(conversationId)
							.set(ConversationItem(conversationId,
							                      partnerId = likedCardItem.userId,
							                      partnerName = likedCardItem.name,
							                      partnerPhotoUrl = likedCardItem.mainPhotoUrl,
							                      lastMessageTimestamp = null))

							.addOnSuccessListener { emitter.onSuccess(true) }
							.addOnFailureListener { emitter.onError(it) }

					}

					else {
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(USER_LIKED_COLLECTION_REFERENCE)
							.document(likedCardItem.userId)
							.set(likedCardItem)
						emitter.onSuccess(false)
					}

			}.addOnFailureListener {
				Log.wtf(TAG, "check match fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	private fun handleMatch(likedCardItem: CardItem, currentCardItem: CardItem){
		//add to matches collection for liked user
		addToMatchCollection(likedCardItem, currentCardItem)

		//add to matches collection for CURRENT user
		addToMatchCollection(currentCardItem, likedCardItem)

		//remove from likes collection for liked user
		//note:uncomment for release
		//deleteFromLikesCollection(likedCardItem, currentCardItem)

		//remove from likes collection for CURRENT user
		deleteFromLikesCollection(currentCardItem.userId, likedCardItem.userId)

		Log.wtf(TAG, "match handle executed")
	}

	private fun addToMatchCollection(userForWhichToAdd: CardItem, whomToAdd: CardItem){
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(userForWhichToAdd.userId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(whomToAdd.userId)
			.set(whomToAdd)
	}

	private fun deleteFromLikesCollection(userIdForWhichDelete: String, whomToDeleteId: String){
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(userIdForWhichDelete)
			.collection(USER_LIKED_COLLECTION_REFERENCE)
			.document(whomToDeleteId)
			.delete()
	}

	private fun setupConversationForUser(conversationId: String, likedUserId: String){
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(likedUserId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
			.set(ConversationItem(conversationId,
			                      partnerId = currentUserItem.userId,
			                      partnerName = currentUserItem.name,
			                      partnerPhotoUrl = currentUserItem.mainPhotoUrl,
			                      lastMessageTimestamp = null))
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

	/* return filtered all users list from already written ids as List<UserItem> */
	private fun filterUsers(cardItemList: List<CardItem>, ids: List<String>): List<CardItem>{
		val filteredUsersList = ArrayList<CardItem>()
		for (card in cardItemList)
			if (!ids.contains(card.userId))
				filteredUsersList.add(card)
		return filteredUsersList
	}


	private fun convertUserItemToCardItem(userItem: UserItem) = CardItem(userItem.name,
	                                                                     userItem.mainPhotoUrl,
	                                                                     userItem.userId)

	//note:debug only
	//
	private fun setLikedForBots(){
		firestore.collection(USERS_COLLECTION_REFERENCE).document("apzjzpbvdj").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("avzcixhglp").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("dtrfbjseuq").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("eoswtmcpul").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("ryknjtobrx").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("snykckkosz").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		Log.wtf("mylogs", "liked for bots executed")
	}

}