/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.02.20 20:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.conversations

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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

	private var currentUserDocRef: DocumentReference
	private var paginateConversationsQuery: Query
	private var currentUserId: String

	init {
		currentUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)

		currentUserId = currentUser.baseUserInfo.userId

		paginateConversationsQuery = currentUserDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.orderBy(CONVERSATION_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.whereEqualTo(CONVERSATION_STARTED_FIELD, true)
			.limit(20)
	}

	companion object{
		// firestore users references
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"

		// firestore conversations reference
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		private const val CONVERSATION_TIMESTAMP_FIELD = "lastMessageTimestamp"
		private const val TAG = "mylogs_ConverRepoImpl"
	}


	private lateinit var paginateLastConversationLoaded: DocumentSnapshot



	override fun deleteConversation(conversationItem: ConversationItem): Completable =
		Completable.create { emitter ->
			val partnerDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(conversationItem.partner.baseUserInfo.city)
				.collection(conversationItem.partner.baseUserInfo.gender)
				.document(conversationItem.partner.baseUserInfo.userId)

			//current user delete from matched list
			currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(conversationItem.partner.baseUserInfo.userId)
				.delete()

			//current user delete from conversations list
			currentUserDocRef
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//partner delete from matched list
			partnerDocRef.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.delete()

			//partner delete from conversations list
			partnerDocRef
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }

			//todo:delete subcollection
			//delete in general
			firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

		}.subscribeOn(Schedulers.io())


	override fun getConversationsList(): Single<List<ConversationItem>> =
		Single.create(SingleOnSubscribe<List<ConversationItem>> { emitter ->
			paginateConversationsQuery
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty){
						val paginateConversationsList = ArrayList<ConversationItem>()
						for (doc in it) {
							paginateConversationsList.add(doc.toObject(ConversationItem::class.java))
						}
						emitter.onSuccess(paginateConversationsList)
						//new cursor position
						paginateLastConversationLoaded = it.documents[it.size() - 1]
						//update query with new cursor position
						paginateConversationsQuery =
							paginateConversationsQuery.startAfter(paginateLastConversationLoaded)
					}
					else emitter.onSuccess(listOf())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())



}