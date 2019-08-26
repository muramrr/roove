package com.mmdev.data.messages

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.model.PhotoAttached
import com.mmdev.domain.messages.repository.ChatRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                             private val storage: FirebaseStorage): ChatRepository{

	companion object{

		// Firebase firestore references
		private const val GENERAL_COLLECTION_REFERENCE = "chats"
		private const val SECONDARY_COLLECTION_REFERENCE = "messages"
		//todo: structure user chats properly
		private const val CHAT_REFERENCE = "user_chat"

		// Firebase Storage references
		private const val URL_STORAGE_REFERENCE = "gs://meetups-c34b0.appspot.com"
		private const val FOLDER_STORAGE_IMG = "images"
	}

	override fun sendMessage(message: Message): Completable {
		return Completable.create { emitter ->
			firestore.collection(GENERAL_COLLECTION_REFERENCE)
				.document(CHAT_REFERENCE)
				.collection(SECONDARY_COLLECTION_REFERENCE)
				.document()
				.set(message)
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(Schedulers.io())
	}

	override fun sendPhoto(message: Message, photo: File): Completable {
		return Completable.create{ emitter ->
			storage.getReferenceFromUrl(URL_STORAGE_REFERENCE)
				.child(FOLDER_STORAGE_IMG)
				.putFile(Uri.fromFile(photo))
				.addOnCompleteListener{ task ->
					if (task.isSuccessful) {
						val downloadUrl = task.result
						val photoAttached = PhotoAttached(downloadUrl!!.toString(), photo.name)
						message.photoAttached = photoAttached
						sendMessage(message)
					}
					emitter.onComplete()
				}
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(Schedulers.io())
	}


	override fun getMessages(): Observable<List<Message>> {
		return Observable.create(ObservableOnSubscribe<List<Message>> { emitter ->
			firestore.collection(GENERAL_COLLECTION_REFERENCE)
				.document(CHAT_REFERENCE)
				.collection(SECONDARY_COLLECTION_REFERENCE)
				.orderBy("timestamp", Query.Direction.DESCENDING)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}

					val messages = arrayListOf<Message>()
                    snapshots?.let {
                        for (doc in snapshots) {
                            messages.add(doc.toObject(Message::class.java))
                        }
                    }

					emitter.onNext(messages)
				}
		}).subscribeOn(Schedulers.io())
	}




}