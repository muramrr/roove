/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.01.20 18:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.chat

import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.chat.entity.PhotoAttachementItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.user.UserItem
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class ChatRepositoryImpl @Inject constructor(private val currentUser: UserItem,
                                             private val firestore: FirebaseFirestore,
                                             private val storage: StorageReference): ChatRepository{

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
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUser.baseUserInfo.city)
				.collection(currentUser.baseUserInfo.gender)
				.document(currentUser.baseUserInfo.userId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.whereEqualTo(CONVERSATION_PARTNER_FIELD, partnerId)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val conversation =
							it.documents[0].toObject(ConversationItem::class.java)!!
						emitter.onSuccess(conversation)
					}
					else emitter.onError(Exception("chatRepository: can't retrive such conversation"))
				}
				.addOnFailureListener { emitter.onError(it) }

		}).subscribeOn(Schedulers.io())
	}

	override fun getMessagesList(conversation: ConversationItem): Observable<List<MessageItem>> {
		this.conversation = conversation
		this.partner = conversation.partner
		Log.wtf(TAG, "conversation set, id = ${conversation.conversationId}")
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
						messages.add(doc.toObject(MessageItem::class.java))
					}
					emitter.onNext(messages)
				}
			emitter.setCancellable{ listener.remove() }
		}).subscribeOn(Schedulers.io())
	}


	override fun sendMessage(messageItem: MessageItem, emptyChat: Boolean?): Completable {
		//Log.wtf("TAG", "is empty recieved? + $emptyChat")
		val conversation = firestore
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
		return Completable.create { emitter ->
			if (emptyChat != null && emptyChat == false)
				conversation.collection(SECONDARY_COLLECTION_REFERENCE)
					.document()
					.set(messageItem)
					.addOnSuccessListener {
						updateLastMessage(messageItem)
						emitter.onComplete()
					}
					.addOnFailureListener { emitter.onError(it) }
			else {
				conversation.get().addOnSuccessListener { documentSnapshot ->
					if (documentSnapshot.exists()) {
						conversation.collection(SECONDARY_COLLECTION_REFERENCE).document()
							.set(messageItem).addOnSuccessListener {
								updateLastMessage(messageItem)
								emitter.onComplete()
							}
					}
					else {
						updateStartedStatus()
						conversation.collection(SECONDARY_COLLECTION_REFERENCE).document()
							.set(messageItem).addOnSuccessListener {
								updateLastMessage(messageItem)
								emitter.onComplete()
							}
					}

				}.addOnFailureListener { emitter.onError(it) }
			}

		}.subscribeOn(Schedulers.io())
	}

	override fun sendPhoto(photoUri: String): Observable<PhotoAttachementItem> {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()+".jpg"
		val storageRef = storage
			.child(GENERAL_FOLDER_STORAGE_IMG)
			.child(conversation.conversationId)
			.child(namePhoto)
		return Observable.create(ObservableOnSubscribe<PhotoAttachementItem>{ emitter ->
			val uploadTask = storageRef.putFile(Uri.parse(photoUri))
				.addOnSuccessListener {
					storageRef.downloadUrl.addOnSuccessListener{
						val photoAttached = PhotoAttachementItem(it.toString(), namePhoto)
						emitter.onNext(photoAttached)
					}
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable{ uploadTask.cancel() }
		}).subscribeOn(Schedulers.io())
	}


	private fun updateStartedStatus() {
		// for current
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.update(CONVERSATION_STARTED_FIELD, true)
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(partner.userId)
			.update(CONVERSATION_STARTED_FIELD, true)


		// for partner
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partner.city)
			.collection(partner.gender)
			.document(partner.userId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
			.update(CONVERSATION_STARTED_FIELD, true)
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partner.city)
			.collection(partner.gender)
			.document(partner.userId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.userId)
			.update(CONVERSATION_STARTED_FIELD, true)
		Log.wtf(TAG, "convers status updated")
	}

	private fun updateLastMessage(messageItem: MessageItem) {
		val cur = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
		val par = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partner.city)
			.collection(partner.gender)
			.document(partner.userId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversation.conversationId)
		if (messageItem.photoAttachementItem != null) {
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
		Log.wtf(TAG, "last message updated")
	}

}