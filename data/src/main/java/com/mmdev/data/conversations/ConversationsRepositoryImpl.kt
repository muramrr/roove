/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.conversations


import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.schedulers.ExecuteSchedulers
import com.mmdev.data.user.UserWrapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class ConversationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                                      userWrapper: UserWrapper):
		ConversationsRepository, BaseRepositoryImpl(firestore, userWrapper) {

	private var initialConversationsQuery: Query = currentUserDocRef
		.collection(CONVERSATIONS_COLLECTION_REFERENCE)
		.orderBy(CONVERSATION_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, true)
		.limit(20)


	private lateinit var paginateLastConversationLoaded: DocumentSnapshot
	private lateinit var paginateConversationsQuery: Query


	override fun deleteConversation(conversationItem: ConversationItem): Completable =
		Completable.create { emitter ->
			reInit()
			val partnerDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(conversationItem.partner.city)
				.collection(conversationItem.partner.gender)
				.document(conversationItem.partner.userId)

			//current user delete from matched list
			currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(conversationItem.partner.userId)
				.delete()

			//current user delete from conversations list
			currentUserDocRef
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//add to skipped collection
			currentUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(conversationItem.partner.userId)
				.set(mapOf(USER_ID_FIELD to conversationItem.partner.userId))

			//partner delete from matched list
			partnerDocRef.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.delete()

			//partner delete from conversations list
			partnerDocRef
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//add to skipped collection
			partnerDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.set(mapOf(USER_ID_FIELD to currentUserId))

			//mark that conversation no need to be exists
			firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.set(mapOf(CONVERSATION_DELETED_FIELD to true))
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(ExecuteSchedulers.io())

	override fun getConversationsList(): Single<List<ConversationItem>> =
		Single.create(SingleOnSubscribe<List<ConversationItem>> { emitter ->
			reInit()
			initialConversationsQuery
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val conversationsList = mutableListOf<ConversationItem>()
						for (doc in it) {
							conversationsList.add(doc.toObject(ConversationItem::class.java))
						}
						emitter.onSuccess(conversationsList)
						//new cursor position
						paginateLastConversationLoaded = it.documents[it.size() - 1]
						//init pagination query
						paginateConversationsQuery =
							initialConversationsQuery.startAfter(paginateLastConversationLoaded)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(ExecuteSchedulers.io())

	override fun getMoreConversationsList(): Single<List<ConversationItem>> =
		Single.create(SingleOnSubscribe<List<ConversationItem>> { emitter ->
			paginateConversationsQuery
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val paginateConversationsList = mutableListOf<ConversationItem>()
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
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(ExecuteSchedulers.io())

	override fun reInit() {
		super.reInit()
		initialConversationsQuery = currentUserDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.orderBy(CONVERSATION_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.whereEqualTo(CONVERSATION_STARTED_FIELD, true)
			.limit(20)
	}
}