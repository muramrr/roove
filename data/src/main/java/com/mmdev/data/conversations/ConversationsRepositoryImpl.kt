/*
 * Created by Andrii Kovalchuk on 26.10.19 18:53
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.conversations

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.user.model.UserItem
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class ConversationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                                      private val currentUserItem: UserItem):
		ConversationsRepository {

	companion object{
		// firestore users references
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"

		// firestore conversations reference
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
	}

	private val currentUserId = currentUserItem.userId


	override fun createConversation(partnerCardItem: CardItem): Single<ConversationItem> {

		return Single.create(SingleOnSubscribe<ConversationItem> { emitter ->
			//generate id for new conversation
			val conversationId = firestore
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document()
				.id

			val conversationItem = ConversationItem(conversationId,
			                                        partnerCardItem.userId,
			                                        partnerCardItem.name,
			                                        partnerCardItem.mainPhotoUrl)

			//set conversation for current user
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
				.set(conversationItem)

			//set conversation for another user
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(partnerCardItem.userId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
				.set(ConversationItem(conversationId,
				                      currentUserItem.userId,
				                      currentUserItem.name,
				                      currentUserItem.mainPhotoUrl))

			//set "started" status to conversations for both users
			//note: uncomment for release
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(partnerCardItem.userId)
				.update("conversationStarted", true)

			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(partnerCardItem.userId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.update("conversationStarted", true)

				.addOnSuccessListener { emitter.onSuccess(conversationItem) }
				.addOnFailureListener { emitter.onError(it) }

		}).subscribeOn(Schedulers.io())
	}


	override fun deleteConversation(conversationItem: ConversationItem): Completable {
		return Completable.create { emitter ->

			//delete in general
			firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//delete in current user section
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//delete in partner section
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(conversationItem.partnerId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(Schedulers.io())
	}

	override fun getConversationsList(): Observable<List<ConversationItem>> {
		return Observable.create(ObservableOnSubscribe<List<ConversationItem>> { emitter ->
			val listener = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					val conversations = ArrayList<ConversationItem>()
					for (doc in snapshots!!) {
						conversations.add(doc.toObject(ConversationItem::class.java))
					}
					emitter.onNext(conversations)
				}
			emitter.setCancellable{ listener.remove() }
		}).subscribeOn(Schedulers.io())
	}


}