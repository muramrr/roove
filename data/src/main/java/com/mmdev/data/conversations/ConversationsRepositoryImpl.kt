/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.02.20 15:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.conversations

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.user.UserItem
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class ConversationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                                      currentUser: UserItem):
		ConversationsRepository {

	private var currentUserDocReference: DocumentReference

	init {
		currentUserDocReference = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
	}

	companion object{
		// firestore users references
		private const val USERS_COLLECTION_REFERENCE = "users"

		// firestore conversations reference
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		private const val TAG = "mylogs_ConverRepoImpl"
	}



	override fun deleteConversation(conversationItem: ConversationItem): Completable {
		return Completable.create { emitter ->

			//delete in general
			firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//delete in current user section
			currentUserDocReference
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

	override fun getConversationsList() =
		Single.create(SingleOnSubscribe<List<ConversationItem>> { emitter ->
			currentUserDocReference
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.orderBy("lastMessageTimestamp", Query.Direction.DESCENDING)
				.whereEqualTo(CONVERSATION_STARTED_FIELD, true)
				.get()
				.addOnSuccessListener { docSnapshot ->
					val conversations = mutableListOf<ConversationItem>()
					if (!docSnapshot.isEmpty)
						for (doc in docSnapshot!!) {
							conversations.add(doc.toObject(ConversationItem::class.java))
						}
					emitter.onSuccess(conversations)
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())



}