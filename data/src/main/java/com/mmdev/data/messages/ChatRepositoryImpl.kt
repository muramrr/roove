package com.mmdev.data.messages

import android.net.Uri
import android.text.format.DateFormat
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.model.PhotoAttached
import com.mmdev.domain.messages.repository.ChatRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

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

	override fun sendPhoto(photoUri: String): Observable<PhotoAttached> {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()+".jpg"
		val storageRef = storage
			.getReferenceFromUrl(URL_STORAGE_REFERENCE)
			.child(FOLDER_STORAGE_IMG)
			.child(namePhoto)

		return Observable.create(ObservableOnSubscribe<PhotoAttached>{ emitter ->
			val uploadTask = storageRef.putFile(Uri.parse(photoUri))
				.addOnSuccessListener {
					storageRef.downloadUrl.addOnSuccessListener{
						val photoAttached = PhotoAttached(it.toString(), namePhoto)
						emitter.onNext(photoAttached)
					}
				}
				.addOnFailureListener { emitter.onError(it) }
			emitter.setCancellable{ uploadTask.cancel() }
		}).subscribeOn(Schedulers.io())
	}


	override fun getMessages(): Observable<List<Message>> {
		return Observable.create(ObservableOnSubscribe<List<Message>> { emitter ->
			val listener = firestore.collection(GENERAL_COLLECTION_REFERENCE)
				.document(CHAT_REFERENCE)
				.collection(SECONDARY_COLLECTION_REFERENCE)
				.orderBy("timestamp")
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
					emitter.onError(e)
					Log.wtf("mylogs", "Listen failed.", e)
					return@addSnapshotListener
				}
				val messages = ArrayList<Message>()
				Log.wtf("mylogs", "size snapshot ${snapshots!!.size()}")
				for (doc in snapshots) {
					messages.add(doc.toObject(Message::class.java))
				}
				emitter.onNext(messages)
			}
			emitter.setCancellable{ listener.remove() }
		}).subscribeOn(Schedulers.io())
	}




}