/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
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
import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.model.PhotoAttachementItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.user.model.UserItem
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class ChatRepositoryImpl @Inject constructor(private val currentUserItem: UserItem,
                                             private val firestore: FirebaseFirestore,
                                             private val storage: StorageReference): ChatRepository{

	companion object {
		// Firebase firestore references
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val SECONDARY_COLLECTION_REFERENCE = "messages"

		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		// Firebase Storage references
		private const val GENERAL_FOLDER_STORAGE_IMG = "images"
	}


	private val currentUserId = currentUserItem.userId

	private var conversationId = ""
	private var partnerId = ""



	override fun getConversationWithPartner(partnerId: String): Single<ConversationItem> {
		this.partnerId = partnerId
		return Single.create(SingleOnSubscribe<ConversationItem> { emitter ->
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.whereEqualTo("partnerId", partnerId)
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val conversation =
							it.documents[0].toObject(ConversationItem::class.java)!!
						emitter.onSuccess(conversation)
					}
					else emitter.onError(Exception("can't retrive such conversation"))
				}
				.addOnFailureListener { emitter.onError(it) }

		}).subscribeOn(Schedulers.io())
	}

	override fun getMessagesList(conversation: ConversationItem): Observable<List<MessageItem>> {
		this.conversationId = conversation.conversationId
		this.partnerId = conversation.partnerId
		Log.wtf("mylogs", "conversation set, id = $conversationId")
		return Observable.create(ObservableOnSubscribe<List<MessageItem>> { emitter ->
			val listener = firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
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
		Log.wtf("mylogs", "is empty recieved? + $emptyChat")
		val conversation = firestore
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
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
			.child(conversationId)
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
			.document(currentUserId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
			.update("conversationStarted", true)
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(partnerId)
			.update("conversationStarted", true)
		// for partner
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partnerId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
			.update("conversationStarted", true)
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partnerId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.document(currentUserId)
			.update("conversationStarted", true)
		Log.wtf("mylogs", "convers status updated")
	}

	private fun updateLastMessage(messageItem: MessageItem) {
		val cur = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
		val par = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(partnerId)
			.collection(CONVERSATIONS_COLLECTION_REFERENCE)
			.document(conversationId)
		if (messageItem.photoAttachementItem != null) {
			// for current
			cur.update("lastMessageText", "Photo")
			cur.update("lastMessageTimestamp", messageItem.timestamp)
			// for partner
			par.update("lastMessageText", "Photo")
			par.update("lastMessageTimestamp", messageItem.timestamp)
		}
		else {
			// for current
			cur.update("lastMessageText", messageItem.text)
			cur.update("lastMessageTimestamp", messageItem.timestamp)
			// for partner
			par.update("lastMessageText", messageItem.text)
			par.update("lastMessageTimestamp", messageItem.timestamp)
		}
		Log.wtf("mylogs", "last message updated")
	}

}