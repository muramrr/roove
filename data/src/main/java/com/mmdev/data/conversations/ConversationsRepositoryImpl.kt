package com.mmdev.data.conversations

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository
import com.mmdev.business.user.model.UserItem
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

@Singleton
class ConversationsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                                      private val currentUserItem: UserItem):
		ConversationsRepository{

	companion object{
		// firestore users references
		private const val USERS_COLLECTION_REFERENCE = "users"

		// firestore conversations reference
		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
	}

	private val currentUserId = currentUserItem.userId


	override fun createConversation(partnerUserItem: UserItem): Single<ConversationItem> {

		return Single.create(SingleOnSubscribe<ConversationItem> { emitter ->

			//if conversation with this user does not exists
			if(!firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.whereEqualTo("partnerId", partnerUserItem.userId)
				.get().isSuccessful) {

				//generate id for new conversation
				val conversationId =
					firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE).document().id

				val conversationItem = ConversationItem(conversationId,
				                                        partnerUserItem.userId,
				                                        partnerUserItem.name,
				                                        partnerUserItem.mainPhotoUrl)

				//set conversation for current user
				firestore.collection(USERS_COLLECTION_REFERENCE).document(currentUserId)
					.collection(CONVERSATIONS_COLLECTION_REFERENCE).document(conversationId)
					.set(conversationItem)

				//set conversation for another user
				firestore.collection(USERS_COLLECTION_REFERENCE).document(partnerUserItem.userId)
					.collection(CONVERSATIONS_COLLECTION_REFERENCE).document(conversationId)
					.set(ConversationItem(conversationId,
					                      currentUserItem.userId,
					                      currentUserItem.name,
					                      currentUserItem.mainPhotoUrl))


					.addOnSuccessListener { emitter.onSuccess(conversationItem) }
					.addOnFailureListener { emitter.onError(it) }
			}
			//if exists = get()
			else {
				firestore.collection(USERS_COLLECTION_REFERENCE)
					.document(currentUserId)
					.collection(CONVERSATIONS_COLLECTION_REFERENCE)
					.whereEqualTo("partnerId", partnerUserItem.userId)
					.get()
					.addOnSuccessListener {
						val conversationItem = it.documents[0]
							.toObject(ConversationItem::class.java)!!
						emitter.onSuccess(conversationItem)
					}
					.addOnFailureListener { emitter.onError(it) }
			}

		}).subscribeOn(Schedulers.io())
	}

	override fun deleteConversation(conversationItem: ConversationItem): Completable {
		return Completable.create { emitter ->

			//delete in general
			firestore.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//delete in current user section
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

			//delete in partner section
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(conversationItem.partnerId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(conversationItem.conversationId)
				.delete()

				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }

		}.subscribeOn(Schedulers.io())
	}

	override fun getConversationsList(): Observable<List<ConversationItem>> {

		return Observable.create(ObservableOnSubscribe<List<ConversationItem>> { emitter ->
			val listener = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						Log.wtf("mylogs", "Listen failed.", e)
						return@addSnapshotListener
					}
					val conversations = ArrayList<ConversationItem>()
					Log.wtf("mylogs", "size snapshot ${snapshots!!.size()}")
					for (doc in snapshots) {
						conversations.add(doc.toObject(ConversationItem::class.java))
					}
					emitter.onNext(conversations)
				}
			emitter.setCancellable{ listener.remove() }
		}).subscribeOn(Schedulers.io())
	}


}