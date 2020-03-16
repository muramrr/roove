/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.03.20 15:39
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.chat

import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.storage.StorageReference
import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.PhotoItem
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.user.UserWrapper
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [observeNewMessages] method uses firebase snapshot listener
 * read more about local changes and writing to backend with "latency compensation"
 * @link https://firebase.google.com/docs/firestore/query-data/listen
 */

@Singleton
class ChatRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                             private val storage: StorageReference,
                                             userWrapper: UserWrapper):
		ChatRepository, BaseRepositoryImpl(firestore, userWrapper) {

	companion object {
		private const val MESSAGES_COLLECTION_REFERENCE = "messages"
		private const val MESSAGE_TIMESTAMP_FIELD = "timestamp"
	}

	private var conversation = ConversationItem()
	private var partner = BaseUserInfo()

	private lateinit var initialChatQuery : Query

	private lateinit var paginateChatQuery: Query
	private lateinit var paginateLastLoadedMessage: DocumentSnapshot


	override fun loadMessages(conversation: ConversationItem): Single<List<MessageItem>> {
		this.conversation = conversation
		this.partner = conversation.partner

		initialChatQuery = firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.collection(MESSAGES_COLLECTION_REFERENCE)
			.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.limit(20)

		return Single.create(SingleOnSubscribe<List<MessageItem>> { emitter ->
			initialChatQuery
				.get()
				.addOnSuccessListener {
					//check if there is messages first
					//it will throw exception IndexOutOfRange without this statement
					if (!it.isEmpty) {
						val initialMessageList = mutableListOf<MessageItem>()
						var message: MessageItem
						for (doc in it) {
							message = doc.toObject(MessageItem::class.java)
							message.timestamp = (message.timestamp as Timestamp).toDate()
							initialMessageList.add(message)
						}
						emitter.onSuccess(initialMessageList)

						//new cursor position
						paginateLastLoadedMessage = it.documents[it.size() - 1]
						//update query with new cursor position
						paginateChatQuery =
							initialChatQuery.startAfter(paginateLastLoadedMessage)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }

		}).subscribeOn(Schedulers.io())
	}

	override fun loadMoreMessages(): Single<List<MessageItem>> {
		return Single.create(SingleOnSubscribe<List<MessageItem>> { emitter ->
			paginateChatQuery
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val paginateMessagesList = mutableListOf<MessageItem>()
						var message: MessageItem
						for (doc in it) {
							message = doc.toObject(MessageItem::class.java)
							message.timestamp = (message.timestamp as Timestamp).toDate()
							paginateMessagesList.add(message)
						}
						emitter.onSuccess(paginateMessagesList)
						//new cursor position
						paginateLastLoadedMessage = it.documents[it.size() - 1]
						//update query with new cursor position
						paginateChatQuery =
							paginateChatQuery.startAfter(paginateLastLoadedMessage)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}

	override fun observeNewMessages(conversation: ConversationItem): Observable<MessageItem> {
		super.reInit()
		this.conversation = conversation
		this.partner = conversation.partner
		return Observable.create(ObservableOnSubscribe<MessageItem> { emitter ->
			val listener = firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversation.conversationId)
				.collection(MESSAGES_COLLECTION_REFERENCE)
				.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}

					if (snapshots != null && snapshots.documents.isNotEmpty()) {
						//if sent from current device
						if (snapshots.metadata.hasPendingWrites()) {
							for (dc in snapshots.documentChanges) {
								if (dc.type == DocumentChange.Type.ADDED) {
									val message = dc.document.toObject(MessageItem::class.java)
									message.timestamp = (message.timestamp as Timestamp?)?.toDate()
									Log.wtf(TAG, "Added: ${dc.document["text"]}")
									emitter.onNext(message)
								}
							}
						}
						//partner send msg
						else if (snapshots.documents[0].get("sender.userId") != currentUser.baseUserInfo.userId) {
							val message = snapshots.documents[0].toObject(MessageItem::class.java)!!
							message.timestamp = (message.timestamp as Timestamp).toDate()
							//check if last message was loaded with pagination before
							Log.wtf(TAG, "mapped message text from partner: ${message.text}")
							emitter.onNext(message)
						}
					}
					else Log.wtf(TAG, "snapshots is null or empty")
				}
			emitter.setCancellable { listener.remove() }
		}).subscribeOn(Schedulers.io())
	}

	override fun sendMessage(messageItem: MessageItem, emptyChat: Boolean?): Completable {
		//Log.wtf("TAG", "is empty recieved? + $emptyChat")
		val conversation = firestore
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)

		messageItem.timestamp = FieldValue.serverTimestamp()

		return Completable.create { emitter ->
			conversation.collection(MESSAGES_COLLECTION_REFERENCE)
				.document()
				.set(messageItem)
				.addOnSuccessListener {
					updateLastMessage(messageItem)
					if (emptyChat != null && emptyChat == true) updateStartedStatus()
					emitter.onComplete()
				}
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(Schedulers.io())
	}

	override fun uploadMessagePhoto(photoUri: String): Observable<PhotoItem> {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()+".jpg"
		val storageRef = storage
			.child(GENERAL_FOLDER_STORAGE_IMG)
			.child(SECONDARY_FOLDER_STORAGE_IMG)
			.child(conversation.conversationId)
			.child(namePhoto)
		return Observable.create(ObservableOnSubscribe<PhotoItem> { emitter ->
			//Log.wtf(TAG, "upload photo observable called")
			val uploadTask = storageRef.putFile(Uri.parse(photoUri))
				.addOnSuccessListener {
					storageRef.downloadUrl.addOnSuccessListener {
						val photoAttached = PhotoItem(
								namePhoto,
								it.toString())
						//Log.wtf(TAG, "photo uploaded: $photoAttached")
						emitter.onNext(photoAttached)
						emitter.onComplete()
					}
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable { uploadTask.cancel() }
		}).subscribeOn(Schedulers.io())
	}

	private fun updateStartedStatus() {
		super.reInit()
		// for current
		currentUserDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.update(CONVERSATION_STARTED_FIELD, true)
		currentUserDocRef
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(partner.userId)
			.update(CONVERSATION_STARTED_FIELD, true)


		// for partner
		val partnerUserReference = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partner.city)
			.collection(partner.gender)
			.document(partner.userId)

		partnerUserReference
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.update(CONVERSATION_STARTED_FIELD, true)
		partnerUserReference
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.userId)
			.update(CONVERSATION_STARTED_FIELD, true)
		//Log.wtf(TAG, "convers status updated")
	}

	private fun updateLastMessage(messageItem: MessageItem) {
		super.reInit()
		val cur = currentUserDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)

		val par = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partner.city)
			.collection(partner.gender)
			.document(partner.userId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)

		if (messageItem.photoItem != null) {
			// for current
			cur.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, "Photo")
			cur.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
			// for partner
			par.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, "Photo")
			par.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
		}
		else {
			// for current
			cur.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, messageItem.text)
			cur.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
			// for partner
			par.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, messageItem.text)
			par.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
		}
		//Log.wtf(TAG, "last message updated")
	}

}