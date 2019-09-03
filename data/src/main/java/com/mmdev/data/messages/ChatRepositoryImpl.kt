package com.mmdev.data.messages

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.storage.FirebaseStorage
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.model.PhotoAttached
import com.mmdev.domain.messages.repository.ChatRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class ChatRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                             private val storage: FirebaseStorage): ChatRepository{



	@ServerTimestamp val mTimestamp: Date? = null
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
		message.timestamp = mTimestamp!!
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
		val storagRef = storage.getReferenceFromUrl(URL_STORAGE_REFERENCE)
			.child(FOLDER_STORAGE_IMG)
		return Completable.create{ emitter ->
				storagRef.child(photo.name)
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
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						Log.wtf("mylogs", "Listen failed.", e)
						return@addSnapshotListener
					}
					val messages = ArrayList<Message>()
					Log.wtf("mylogs", "size snapshot ${snapshots!!.size()}")
					for (doc in snapshots) {
						doc?.let { messages.add(it.toObject(Message::class.java)) }
					}
					emitter.onNext(messages)
				}

		}).subscribeOn(Schedulers.io())
	}




}