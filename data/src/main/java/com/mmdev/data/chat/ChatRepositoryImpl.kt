/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 15:53
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
import com.mmdev.business.chat.entity.PhotoAttachmentItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * [observeNewMessages] method uses firebase snapshot listener
 * read more about local changes and writing to backend with "latency compensation"
 * @link https://firebase.google.com/docs/firestore/query-data/listen
 */


class ChatRepositoryImpl @Inject constructor(private val currentUser: UserItem,
                                             private val firestore: FirebaseFirestore,
                                             private val storage: StorageReference): ChatRepository{

	private var currentUserDocRef: DocumentReference

	init {
		currentUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
	}

	companion object {
		// Firebase firestore references
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val SECONDARY_COLLECTION_REFERENCE = "messages"

		//firestore conversation fields for updating
		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		private const val CONVERSATION_LAST_MESSAGE_TEXT_FIELD = "lastMessageText"
		private const val CONVERSATION_TIMESTAMP_FIELD = "lastMessageTimestamp"
		private const val MESSAGE_TIMESTAMP_FIELD = "timestamp"

		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		// Firebase Storage references
		private const val GENERAL_FOLDER_STORAGE_IMG = "images"
		private const val SECONDARY_FOLDER_STORAGE_IMG = "conversations"

		private const val TAG = "mylogs_ChatRepoImpl"
	}

	private var conversation = ConversationItem()
	private var partner = BaseUserInfo()

	private val messagesList = mutableListOf<MessageItem>()

	private lateinit var paginateChatQuery: Query
	private lateinit var paginateLastLoadedMessage: DocumentSnapshot



	override fun loadMessages(conversation: ConversationItem): Single<List<MessageItem>> {
		this.conversation = conversation
		this.partner = conversation.partner.baseUserInfo

		//check is this first call
		if (!this::paginateChatQuery.isInitialized)
			paginateChatQuery = firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversation.conversationId)
				.collection(SECONDARY_COLLECTION_REFERENCE)
				.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
				.limit(20)

		//Log.wtf(TAG, "load messages called")

		return Single.create(SingleOnSubscribe<List<MessageItem>> { emitter ->
			paginateChatQuery
				.get()
				.addOnSuccessListener {
					//check if there is messages first
					//it will throw exception IndexOutOfRange without this statement
					if (!it.isEmpty) {
						val paginateMessageList = ArrayList<MessageItem>()
						for (doc in it) {
							val message = doc.toObject(MessageItem::class.java)
							message.timestamp = (message.timestamp as Timestamp?)?.toDate()
							if (!messagesList.contains(message))
								paginateMessageList.add(message)
						}
						emitter.onSuccess(paginateMessageList)
						messagesList.addAll(paginateMessageList)

						//new cursor position
						paginateLastLoadedMessage = it.documents[it.size() - 1]
						//update query with new cursor position
						paginateChatQuery =
							paginateChatQuery.startAfter(paginateLastLoadedMessage)
					}
				}
				.addOnFailureListener { emitter.onError(it) }

		}).subscribeOn(Schedulers.io())
	}

	override fun observeNewMessages(conversation: ConversationItem): Observable<MessageItem> {
		this.conversation = conversation
		this.partner = conversation.partner.baseUserInfo

		return Observable.create(ObservableOnSubscribe<MessageItem> { emitter ->
			val listener = firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversation.conversationId)
				.collection(SECONDARY_COLLECTION_REFERENCE)
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
									messagesList.add(0, message)
								}
							}
						}
						//partner send msg
						else if (snapshots.documents[0].get("sender.userId") != currentUser.baseUserInfo.userId) {
							Log.wtf(TAG, "received message from partner")
							val message = snapshots.documents[0].toObject(MessageItem::class.java)!!
							message.timestamp = (message.timestamp as Timestamp?)?.toDate()
							//check if last message was loaded with pagination before
							Log.wtf(TAG, "mapped message text: ${message.text}")
							if (!messagesList.contains(message)) {
								emitter.onNext(message)
								messagesList.add(0, message)
							}
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
			conversation.collection(SECONDARY_COLLECTION_REFERENCE)
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


	override fun uploadMessagePhoto(photoUri: String): Observable<PhotoAttachmentItem> {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()+".jpg"
		val storageRef = storage
			.child(GENERAL_FOLDER_STORAGE_IMG)
			.child(SECONDARY_FOLDER_STORAGE_IMG)
			.child(conversation.conversationId)
			.child(namePhoto)
		return Observable.create(ObservableOnSubscribe<PhotoAttachmentItem> { emitter ->
			//Log.wtf(TAG, "upload photo observable called")
			val uploadTask = storageRef.putFile(Uri.parse(photoUri))
				.addOnSuccessListener {
					storageRef.downloadUrl.addOnSuccessListener {
						val photoAttached = PhotoAttachmentItem(namePhoto, it.toString())
						//Log.wtf(TAG, "photo uploaded: $photoAttached")
						emitter.onNext(photoAttached)
					}
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable { uploadTask.cancel() }
		}).subscribeOn(Schedulers.io())
	}


	private fun updateStartedStatus() {
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
		val cur = currentUserDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)

		val par = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partner.city)
			.collection(partner.gender)
			.document(partner.userId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)

		if (messageItem.photoAttachmentItem != null) {
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