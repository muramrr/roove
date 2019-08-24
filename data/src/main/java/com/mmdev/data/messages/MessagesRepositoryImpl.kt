package com.mmdev.data.messages

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.repository.MessagesRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(private val db: FirebaseFirestore): MessagesRepository{

    override fun sendMessage(message: Message): Completable {
        return Completable.create { emitter ->
            db.collection("messages")
                    .add(message)
                    .addOnSuccessListener { emitter.onComplete() }
                    .addOnFailureListener { emitter.onError(it) }
        }.subscribeOn(Schedulers.io())
    }

    override fun getMessages(): Observable<List<Message>> {
        return Observable.create(ObservableOnSubscribe<List<Message>> { emitter ->
            db.collection("messages")
                    .orderBy("sent", Query.Direction.DESCENDING)
                    .addSnapshotListener { snapshots, e ->
                        if (e != null) {
                            emitter.onError(e)
                            return@addSnapshotListener
                        }

                        val messages = arrayListOf<Message>()
                        //todo: fix getting messages
//                        snapshots?.let {
//                            for (doc in snapshots) {
//                                messages
//                                    .add(Message(doc.get("message")!!,
//                                        doc.getString("sender")!!,
//                                        doc.getLong("sent")!!)
//                                )
//                            }
//                        }

                        emitter.onNext(messages)
                    }
        }).subscribeOn(Schedulers.io())
    }
}