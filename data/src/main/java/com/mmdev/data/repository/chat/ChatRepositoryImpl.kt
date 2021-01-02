/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.storage.StorageReference
import com.mmdev.business.chat.ChatRepository
import com.mmdev.business.chat.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.data.PhotoItem
import com.mmdev.business.user.UserItem
import com.mmdev.data.core.BaseRepository
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.firebase.asSingle
import com.mmdev.data.core.firebase.executeAndDeserializeSingle
import com.mmdev.data.core.firebase.setAsCompletable
import com.mmdev.data.core.firebase.updateAsCompletable
import com.mmdev.data.core.log.logDebug
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate
import java.util.*
import javax.inject.Inject

/**
 * [ChatRepository] implementation
 * [observeNewMessages] method uses firebase snapshot listener
 * read more about local changes and writing to backend with "latency compensation"
 * @link https://firebase.google.com/docs/firestore/query-data/listen
 */

class ChatRepositoryImpl @Inject constructor(
	private val fs: FirebaseFirestore,
	private val storage: StorageReference
): BaseRepository(), ChatRepository {
	
	companion object {
		private const val CONVERSATION_PARTNER_ONLINE_FIELD = "partnerOnline"
		private const val CONVERSATION_UNREAD_COUNT_FIELD = "unreadCount"
		private const val MESSAGES_COLLECTION = "messages"
		private const val MESSAGE_TIMESTAMP_FIELD = "timestamp"
		private const val MESSAGE_SENDER_ID_FILED = "sender.userId"
		
		private const val SOURCE_SERVER = "Server"
		private const val SOURCE_LOCAL = "Local"
	}
	
	private fun messagesQuery(conversation: ConversationItem, cursorPosition: Int): Query =
		fs.collection(CONVERSATIONS_COLLECTION)
			.document(conversation.conversationId)
			.collection(MESSAGES_COLLECTION)
			.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.limit(20)
			.startAfter(cursorPosition)
	
	private var isPartnerOnline = false
	
	private var isSnapshotInitiated = false
	
	override fun loadMessages(
		conversation: ConversationItem,
		cursorPosition: Int
	): Single<List<MessageItem>> = messagesQuery(conversation, cursorPosition)
		.executeAndDeserializeSingle(MessageItem::class.java)
	
	
	
	override fun observeNewMessages(
		user: UserItem,
		conversation: ConversationItem
	): Observable<MessageItem> = ObservableCreate<MessageItem> { emitter ->
		val listener = fs.collection(CONVERSATIONS_COLLECTION)
			.document(conversation.conversationId)
			.collection(MESSAGES_COLLECTION)
			.orderBy(MESSAGE_TIMESTAMP_FIELD, Query.Direction.DESCENDING)
			.limit(1)
			.addSnapshotListener { snapshot, e ->
				if (e != null) {
					emitter.onError(e)
					return@addSnapshotListener
				}
				
				val source = if (snapshot != null && snapshot.metadata.hasPendingWrites()) SOURCE_LOCAL
				else SOURCE_SERVER
				
				if (!snapshot?.documents.isNullOrEmpty() && source == SOURCE_SERVER) {
					
					//documentChanges has various states, parse them
					snapshot!!.documentChanges.forEach { documentChange ->
						if (documentChange.type == DocumentChange.Type.ADDED) {
							
							val document = snapshot.documents.first()
							if (document.getField<String>("recipientId") == user.baseUserInfo.userId) {
								logDebug(TAG, "Message was sent not from this user")
								
								if (isSnapshotInitiated)
									emitter.onNext(document.toObject(MessageItem::class.java))
							}
							
						}
					}
				}
				else {
					logDebug(TAG, "Message comes from $source source")
				}
				
				isSnapshotInitiated = true
				
			}
		emitter.setCancellable { listener.remove() }
	}.subscribeOn(MySchedulers.io())

	//override fun observePartnerOnline(conversationId: String): Observable<Boolean> =
	//	ObservableCreate<Boolean> { emitter ->
	//
	//		val listener = currentUserDocRef
	//			.collection(CONVERSATIONS_COLLECTION)
	//			.document(conversationId)
	//			.addSnapshotListener { snapshot, e ->
	//				if (e != null) {
	//					emitter.onError(e)
	//					return@addSnapshotListener
	//				}
	//				if (snapshot != null && snapshot.exists()) {
	//					snapshot.getBoolean(CONVERSATION_PARTNER_ONLINE_FIELD).let {
	//						if (isPartnerOnline != it) isPartnerOnline = it
	//						emitter.onNext(it)
	//					}
	//
	//				}
	//			}
	//		emitter.setCancellable { listener.remove() }
	//	}.subscribeOn(MySchedulers.io())

	override fun sendMessage(messageItem: MessageItem, emptyChat: Boolean?): Completable {
		
		val conversation = fs
			.collection(CONVERSATIONS_COLLECTION)
			.document(messageItem.conversationId)

		messageItem.timestamp = FieldValue.serverTimestamp()

		return conversation.collection(MESSAGES_COLLECTION)
			.document()
			.setAsCompletable(messageItem)
			//.concatWith {
			//	if (emptyChat != null && emptyChat == true) updateStartedStatus(messageItem)
			//}
			//.andThen {
			//	updateUnreadMessagesCount(messageItem.conversationId)
			//}
			//.andThen {
			//	updateLastMessage(messageItem)
			//}
	}

	override fun uploadMessagePhoto(photoUri: String, conversationId: String): Single<PhotoItem> {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()+".jpg"
		val storageRef = storage
			.child(GENERAL_FOLDER_STORAGE_IMG)
			.child(SECONDARY_FOLDER_STORAGE_IMG)
			.child(conversationId)
			.child(namePhoto)
			.putFile(Uri.parse(photoUri))
			
		return storageRef.asSingle()
			.flatMap { task ->
				task.storage
					.downloadUrl
					.asSingle()
					.map { PhotoItem(fileName = namePhoto, fileUrl = it.toString()) }
			}
	}

	//private fun updateStartedStatus(messageItem: MessageItem) {
	//	// for current
	//	currentUserDocRef
	//		.collection(CONVERSATIONS_COLLECTION)
	//		.document(messageItem.conversationId)
	//		.update(CONVERSATION_STARTED_FIELD, true)
	//
	//	currentUserDocRef
	//		.collection(USER_MATCHED_COLLECTION)
	//		.document(messageItem.recipientId)
	//		.update(CONVERSATION_STARTED_FIELD, true)
	//
	//
	//	// for partner
	//	partnerDocRef
	//		.collection(CONVERSATIONS_COLLECTION)
	//		.document(messageItem.conversationId)
	//		.update(CONVERSATION_STARTED_FIELD, true)
	//	partnerDocRef
	//		.collection(USER_MATCHED_COLLECTION)
	//		.document(currentUser.baseUserInfo.userId)
	//		.update(CONVERSATION_STARTED_FIELD, true)
	//	//Log.wtf(TAG, "convers status updated")
	//}

	//private fun updateLastMessage(user: UserItem, messageItem: MessageItem) {
	//	val cur = fs.collection(USERS_COLLECTION)
	//		.document(user.baseUserInfo.userId)
	//		.collection(CONVERSATIONS_COLLECTION)
	//		.document(messageItem.conversationId)
	//
	//	val par = partnerDocRef
	//		.collection(CONVERSATIONS_COLLECTION)
	//		.document(messageItem.conversationId)
	//
	//	if (messageItem.photoItem != null) {
	//		// for current
	//		cur.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, "Photo")
	//		cur.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
	//		// for partner
	//		par.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, "Photo")
	//		par.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
	//	}
	//	else {
	//		// for current
	//		cur.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, messageItem.text)
	//		cur.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
	//		// for partner
	//		par.update(CONVERSATION_LAST_MESSAGE_TEXT_FIELD, messageItem.text)
	//		par.update(CONVERSATION_TIMESTAMP_FIELD, messageItem.timestamp)
	//	}
	//	//Log.wtf(TAG, "last message updated")
	//}

	private fun updateUnreadMessagesCount(conversation: ConversationItem) =
		fs.collection(USERS_COLLECTION)
			.document(conversation.partner.userId)
			.collection(CONVERSATIONS_COLLECTION)
			.document(conversation.conversationId)
			.updateAsCompletable(CONVERSATION_UNREAD_COUNT_FIELD, FieldValue.increment(1))
}