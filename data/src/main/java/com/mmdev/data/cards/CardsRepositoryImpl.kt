package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.user.model.UserItem
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


/* Created by A on 14.09.2019.*/

/**
 * This is the documentation block about the class
 */
@Singleton
class CardsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              private val currentUserItem: UserItem): CardsRepository {

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
		private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		private const val TAG = "mylogs"
	}

	private val currentUserId = currentUserItem.userId

	private val cardsRepositoryHelper = CardsRepositoryHelper(firestore,
	                                                          currentUserItem.preferedGender,
	                                                          currentUserId)



	//note:debug only
	//
	private fun setLikedForBots(){
		firestore.collection(USERS_COLLECTION_REFERENCE).document("scmxqiwwci").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("vuwtmiegcl").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("qjfarvjwne").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("sqbnmdaiuy").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("nenvmbxeft").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
		firestore.collection(USERS_COLLECTION_REFERENCE).document("frtywqocto").collection(USER_LIKED_COLLECTION_REFERENCE).document(currentUserId).set(currentUserItem)
	}

	/*
	* note: swiped left
	*/
	override fun addToSkipped(skippedCardItem: CardItem) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_SKIPPED_COLLECTION_REFERENCE)
			.document(skippedCardItem.userId)
			.set(skippedCardItem)
	}
	/*
	* note: swiped right
 	* check if users liked each other
	*/
	override fun handlePossibleMatch(likedCardItem: CardItem): Single<Boolean> {
		val likedUserId = likedCardItem.userId
		return Single.create(SingleOnSubscribe<Boolean> { emitter ->

			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(likedUserId)
				.collection(USER_LIKED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.get()
				.addOnSuccessListener{ documentSnapshot ->
					if (documentSnapshot.exists()) {
						//add to match collection
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(likedUserId)
							.collection(USER_MATCHED_COLLECTION_REFERENCE)
							.document(currentUserId)
							.set(CardItem(currentUserItem.name,
							              currentUserItem.mainPhotoUrl,
							              currentUserItem.userId))
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(USER_MATCHED_COLLECTION_REFERENCE)
							.document(likedUserId)
							.set(likedCardItem)

						//remove from like collection
						//note:uncomment for release
		//					firestore.collection(USERS_COLLECTION_REFERENCE)
		//							.document(likedUserId)
		//							.collection(USER_LIKED_COLLECTION_REFERENCE)
		//							.document(mCurrentUserId)
		//							.delete();
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(USER_LIKED_COLLECTION_REFERENCE)
							.document(likedUserId)
							.delete()
						Log.wtf(TAG, "match handle executed")

						emitter.onSuccess(true)
					}

					else {
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(USER_LIKED_COLLECTION_REFERENCE)
							.document(likedUserId)
							.set(likedCardItem)
						emitter.onSuccess(false)
					}

			}.addOnFailureListener {
				Log.wtf(TAG, "handlePossibleMatch fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	/*
	* GET MATCHED [CardItem] LIST
	*/
	override fun getMatchedCardItems(): Observable<List<CardItem>> {
		setLikedForBots()
		return Observable.create(ObservableOnSubscribe<List<CardItem>> { emitter ->
			val listener = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.whereEqualTo("conversationStarted", false)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					val matchedUsersList = ArrayList<CardItem>()
					for (doc in snapshots!!) {
						matchedUsersList.add(doc.toObject(CardItem::class.java))
					}
					emitter.onNext(matchedUsersList)
				}
			emitter.setCancellable { listener.remove() }
		}).subscribeOn(Schedulers.io())
	}

	/* return filtered users list as Single */
	override fun getPotentialCardItems(): Single<List<CardItem>> {
		return Single.zip(cardsRepositoryHelper.getAllUsersCards(),
		                  cardsRepositoryHelper.zipLists(),
		                  BiFunction<List<CardItem>, List<String>, List<CardItem>>
		                  { userList, ids  -> filterUsers(userList, ids) })
			.observeOn(Schedulers.io())

	}

	/* return filtered all users list from already written ids as List<UserItem> */
	private fun filterUsers(cardItemList: List<CardItem>, ids: List<String>): List<CardItem>{
		val filteredUsersList = ArrayList<CardItem>()
		for (card in cardItemList)
			if (!ids.contains(card.userId))
				filteredUsersList.add(card)
		return filteredUsersList
	}

}