/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.02.20 16:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.chat

import android.net.Uri
import android.text.format.DateFormat
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.chat.entity.PhotoAttachmentItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val currentUser: UserItem,
                                             private val firestore: FirebaseFirestore,
                                             private val storage: StorageReference): ChatRepository{

	private var currentUserDocReference: DocumentReference

	init {
		currentUserDocReference = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
	}

	companion object {
		// Firebase firestore references
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val SECONDARY_COLLECTION_REFERENCE = "messages"

		//firestore conversation fields for updating
		private const val CONVERSATION_PARTNER_FIELD = "partner.userId"
		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		private const val CONVERSATION_LASTMESSAGETEXT_FIELD = "lastMessageText"
		private const val CONVERSATION_LASTMESSAGETIMESTAMP_FIELD = "lastMessageTimestamp"

		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		// Firebase Storage references
		private const val GENERAL_FOLDER_STORAGE_IMG = "images"

		private const val TAG = "mylogs_ChatRepoImpl"
	}

	private var conversation = ConversationItem()
	private var partner = BaseUserInfo()

	override fun getConversationWithPartner(partnerId: String): Single<ConversationItem> {
		return Single.create(SingleOnSubscribe<ConversationItem> { emitter ->
			currentUserDocReference
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.whereEqualTo(CONVERSATION_PARTNER_FIELD, partnerId)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val conversation = it.documents[0].toObject(ConversationItem::class.java)!!
						this.conversation = conversation
						emitter.onSuccess(conversation)
					}
					else emitter.onError(Exception("$TAG: can't retrive such conversation"))
				}
				.addOnFailureListener { emitter.onError(it) }

		}).subscribeOn(Schedulers.io())
	}

	override fun getMessagesList(conversation: ConversationItem): Observable<List<MessageItem>> {
		this.conversation = conversation
		this.partner = conversation.partner
		//Log.wtf(TAG, "conversation set, id = ${conversation.conversationId}")
		return Observable.create(ObservableOnSubscribe<List<MessageItem>> { emitter ->
			val listener = firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversation.conversationId)
				.collection(SECONDARY_COLLECTION_REFERENCE)
				.orderBy("timestamp")
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					val messages = ArrayList<MessageItem>()
					for (doc in snapshots!!) {
						val message = doc.toObject(MessageItem::class.java)
						message.timestamp = (message.timestamp as Timestamp?)?.toDate()
						if (!messages.contains(message)) messages.add(message)
					}
					emitter.onNext(messages)
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
			.child(conversation.conversationId)
			.child(namePhoto)
		return Observable.create(ObservableOnSubscribe<PhotoAttachmentItem>{ emitter ->
			val uploadTask = storageRef.putFile(Uri.parse(photoUri))
				.addOnSuccessListener {
					storageRef.downloadUrl.addOnSuccessListener{
						val photoAttached = PhotoAttachmentItem(it.toString(), namePhoto)
						emitter.onNext(photoAttached)
					}
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable{ uploadTask.cancel() }
		}).subscribeOn(Schedulers.io())
	}


	private fun updateStartedStatus() {
		// for current
		currentUserDocReference
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.update(CONVERSATION_STARTED_FIELD, true)
		currentUserDocReference
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
		val cur = currentUserDocReference
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
			cur.update(CONVERSATION_LASTMESSAGETEXT_FIELD, "Photo")
			cur.update(CONVERSATION_LASTMESSAGETIMESTAMP_FIELD, messageItem.timestamp)
			// for partner
			par.update(CONVERSATION_LASTMESSAGETEXT_FIELD, "Photo")
			par.update(CONVERSATION_LASTMESSAGETIMESTAMP_FIELD, messageItem.timestamp)
		}
		else {
			// for current
			cur.update(CONVERSATION_LASTMESSAGETEXT_FIELD, messageItem.text)
			cur.update(CONVERSATION_LASTMESSAGETIMESTAMP_FIELD, messageItem.timestamp)
			// for partner
			par.update(CONVERSATION_LASTMESSAGETEXT_FIELD, messageItem.text)
			par.update(CONVERSATION_LASTMESSAGETIMESTAMP_FIELD, messageItem.timestamp)
		}
		//Log.wtf(TAG, "last message updated")
	}

}