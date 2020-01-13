/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.01.20 18:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.conversations

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.user.UserItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class ConversationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                                      currentUser: UserItem):
		ConversationsRepository {

	companion object{
		// firestore users references
		private const val USERS_COLLECTION_REFERENCE = "users"

		// firestore conversations reference
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
	}

	private val currentUserInfo = currentUser.baseUserInfo


	override fun deleteConversation(conversationItem: ConversationItem): Completable {
		return Completable.create { emitter ->

			//delete in general
			firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//delete in current user section
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserInfo.city)
				.collection(currentUserInfo.gender)
				.document(currentUserInfo.userId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//delete in partner section
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(conversationItem.partner.city)
				.collection(conversationItem.partner.gender)
				.document(conversationItem.partner.userId)
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
				.document(currentUserInfo.city)
				.collection(currentUserInfo.gender)
				.document(currentUserInfo.userId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.whereEqualTo(CONVERSATION_STARTED_FIELD, true)
				.orderBy("lastMessageTimestamp")
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