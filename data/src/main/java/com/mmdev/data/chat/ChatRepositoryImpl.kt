/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 19:50
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
import com.mmdev.business.cards.model.CardItem
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



	private val currentUserId = currentUserItem.userId

	private var conversationId = ""

	companion object {
		// Firebase firestore references
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val SECONDARY_COLLECTION_REFERENCE = "messages"

		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		// Firebase Storage references
		private const val GENERAL_FOLDER_STORAGE_IMG = "images"
	}


	override fun createConversation(partnerCardItem: CardItem): Single<ConversationItem> {

		return Single.create(SingleOnSubscribe<ConversationItem> { emitter ->
			//generate id for new conversation
			val conversationId = firestore
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document()
				.id

			val conversationItem = ConversationItem(conversationId,
			                                        partnerCardItem.userId,
			                                        partnerCardItem.name,
			                                        partnerCardItem.mainPhotoUrl)

			//set conversation for current user
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
				.set(conversationItem)

			//set conversation for another user
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(partnerCardItem.userId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
				.set(ConversationItem(conversationId,
				                      currentUserItem.userId,
				                      currentUserItem.name,
				                      currentUserItem.mainPhotoUrl))


			this.conversationId = conversationId
			//set "started" status to conversations for both users
			//note: uncomment for release
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(partnerCardItem.userId)
				.update("conversationStarted", true)

			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(partnerCardItem.userId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.update("conversationStarted", true)

				.addOnSuccessListener { emitter.onSuccess(conversationItem) }
				.addOnFailureListener { emitter.onError(it) }

		}).subscribeOn(Schedulers.io())
	}

	override fun getMessagesList(conversationId: String): Observable<List<MessageItem>> {
		this.conversationId = conversationId
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


	override fun sendMessage(messageItem: MessageItem): Completable {
		return Completable.create { emitter ->
			firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationId)
				.collection(SECONDARY_COLLECTION_REFERENCE)
				.document()
				.set(messageItem)
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }

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



}