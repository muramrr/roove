/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.repository.chat

import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.storage.StorageReference
import com.mmdev.business.chat.ChatRepository
import com.mmdev.business.chat.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.data.PhotoItem
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.ExecuteSchedulers
import com.mmdev.data.repository.user.UserWrapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
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
                                             userWrapper: UserWrapper
):
		ChatRepository, BaseRepositoryImpl(firestore, userWrapper) {

	private lateinit var partnerDocRef: DocumentReference

	private lateinit var initialChatQuery : Query

	private lateinit var paginateChatQuery: Query
	private lateinit var paginateLastLoadedMessage: DocumentSnapshot

	private var firstMessageItem = MessageItem()

	private var isPartnerOnline = false

	companion object {
		private const val CONVERSATION_PARTNER_ONLINE_FIELD = "partnerOnline"
		private const val CONVERSATION_UNREAD_COUNT_FIELD = "unreadCount"
		private const val MESSAGES_COLLECTION_REFERENCE = "messages"
		private const val MESSAGE_TIMESTAMP_FIELD = "timestamp"
		private const val MESSAGE_SENDER_ID_FILED = "sender.userId"
	}

	override fun loadMessages(conversation: ConversationItem): Single<List<MessageItem>> {
		initialChatQuery = firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.collection(MESSAGES_COLLECTION_REFERENCE)
			.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.limit(20)

		//init partner doc reference
		partnerDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(conversation.partner.city)
			.collection(conversation.partner.gender)
			.document(conversation.partner.userId)

		//reset unread count
		currentUserDocRef.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.update(CONVERSATION_UNREAD_COUNT_FIELD, 0)

		return SingleCreate<List<MessageItem>> { emitter ->
			initialChatQuery
				.get()
				.addOnSuccessListener {
					partnerDocRef.collection(CONVERSATIONS_COLLECTION_REFERENCE)
						.document(conversation.conversationId)
						.update(CONVERSATION_PARTNER_ONLINE_FIELD, true)
					//check if there is messages first
					//it will throw exception IndexOutOfRange without this statement
					if (!it.isEmpty) {
						val initialMessageList = mutableListOf<MessageItem>()
						var message: MessageItem
						for (doc in it) {
							message = doc.toObject(MessageItem::class.java)
							initialMessageList.add(message)
						}
						emitter.onSuccess(initialMessageList)
						firstMessageItem = initialMessageList[0]
						//new cursor position
						paginateLastLoadedMessage = it.documents[it.size() - 1]
						//update query with new cursor position
						paginateChatQuery =
							initialChatQuery.startAfter(paginateLastLoadedMessage)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())
	}

	override fun loadMoreMessages(): Single<List<MessageItem>> =
		SingleCreate<List<MessageItem>> { emitter ->
			paginateChatQuery
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val paginateMessagesList = mutableListOf<MessageItem>()
						var message: MessageItem
						for (doc in it) {
							message = doc.toObject(MessageItem::class.java)
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
		}.subscribeOn(ExecuteSchedulers.io())

	override fun observeNewMessages(conversation: ConversationItem): Observable<MessageItem> {
		super.reInit()

		if (!this::partnerDocRef.isInitialized)
			partnerDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(conversation.partner.city)
				.collection(conversation.partner.gender)
				.document(conversation.partner.userId)

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
					var message: MessageItem
					if (snapshots != null && snapshots.documents.isNotEmpty()) {
						//if sent from current device
						if (snapshots.metadata.hasPendingWrites()) {
							for (dc in snapshots.documentChanges) {
								if (dc.type == DocumentChange.Type.ADDED) {
									message = dc.document.toObject(MessageItem::class.java)
									//Log.wtf(TAG, "Added: ${dc.document["text"]}")
									emitter.onNext(message)
								}
							}
						}
						//partner send msg
						else if (snapshots.documents[0].get(MESSAGE_SENDER_ID_FILED) != currentUser.baseUserInfo.userId) {
							message = snapshots.documents[0].toObject(MessageItem::class.java)!!
							//remove last duplicated message from initLoad and this snapshot listener
							if (message != firstMessageItem)
								emitter.onNext(message)
						}
					}
					else Log.wtf(TAG, "snapshots is null or empty")
				}
			emitter.setCancellable {
				partnerDocRef.collection(CONVERSATIONS_COLLECTION_REFERENCE)
					.document(conversation.conversationId)
					.update(CONVERSATION_PARTNER_ONLINE_FIELD, false)
				listener.remove()
			}
		}).subscribeOn(ExecuteSchedulers.io())
	}

	override fun observePartnerOnline(conversationId: String): Observable<Boolean> =
		ObservableCreate<Boolean> { emitter ->

			val listener = currentUserDocRef
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
				.addSnapshotListener { snapshot, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					if (snapshot != null && snapshot.exists()) {
						snapshot.getBoolean(CONVERSATION_PARTNER_ONLINE_FIELD)?.let {
							if (isPartnerOnline != it)
								isPartnerOnline = it
							emitter.onNext(it)
						}

					}
				}
			emitter.setCancellable { listener.remove() }
		}.subscribeOn(ExecuteSchedulers.io())

	override fun sendMessage(messageItem: MessageItem, emptyChat: Boolean?): Completable {
		//Log.wtf("TAG", "is empty recieved? + $emptyChat")
		val conversation = firestore
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(messageItem.conversationId)

		messageItem.timestamp = FieldValue.serverTimestamp()

		return Completable.create { emitter ->
			conversation.collection(MESSAGES_COLLECTION_REFERENCE)
				.document()
				.set(messageItem)
				.addOnSuccessListener {
					updateLastMessage(messageItem)
					if (emptyChat != null && emptyChat == true) updateStartedStatus(messageItem)
					updateUnreadMessagesCount(messageItem.conversationId)
					emitter.onComplete()
				}
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(ExecuteSchedulers.io())
	}

	override fun uploadMessagePhoto(photoUri: String, conversationId: String): Observable<PhotoItem> {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()+".jpg"
		val storageRef = storage
			.child(GENERAL_FOLDER_STORAGE_IMG)
			.child(SECONDARY_FOLDER_STORAGE_IMG)
			.child(conversationId)
			.child(namePhoto)
		return Observable.create(ObservableOnSubscribe<PhotoItem> { emitter ->
			//Log.wtf(TAG, "upload photo observable called")
			val uploadTask = storageRef.putFile(Uri.parse(photoUri))
				.addOnSuccessListener {
					storageRef.downloadUrl.addOnSuccessListener {
						val photoAttached = PhotoItem(namePhoto, it.toString())
						//Log.wtf(TAG, "photo uploaded: $photoAttached")
						emitter.onNext(photoAttached)
						emitter.onComplete()
					}
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable { uploadTask.cancel() }
		}).subscribeOn(ExecuteSchedulers.io())
	}

	private fun updateStartedStatus(messageItem: MessageItem) {
		super.reInit()
		// for current
		currentUserDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(messageItem.conversationId)
			.update(CONVERSATION_STARTED_FIELD, true)

		currentUserDocRef
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(messageItem.recipientId)
			.update(CONVERSATION_STARTED_FIELD, true)


		// for partner
		partnerDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(messageItem.conversationId)
			.update(CONVERSATION_STARTED_FIELD, true)
		partnerDocRef
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.userId)
			.update(CONVERSATION_STARTED_FIELD, true)
		//Log.wtf(TAG, "convers status updated")
	}

	private fun updateLastMessage(messageItem: MessageItem) {
		super.reInit()
		val cur = currentUserDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(messageItem.conversationId)

		val par = partnerDocRef
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(messageItem.conversationId)

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

	private fun updateUnreadMessagesCount(conversationId: String) {
		if (!isPartnerOnline) {
			partnerDocRef
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
				.update(CONVERSATION_UNREAD_COUNT_FIELD, FieldValue.increment(1))
		}

	}


}