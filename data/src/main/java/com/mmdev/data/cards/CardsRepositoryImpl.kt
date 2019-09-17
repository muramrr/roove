package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.domain.cards.repository.CardsRepository
import com.mmdev.domain.core.model.User
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


/* Created by A on 14.09.2019.*/

/**
 * This is the documentation block about the class
 */
@Singleton
class CardsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              private val currentUser: User): CardsRepository {

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USERS_FILTER = "gender"
		private const val USER_LIKES_COLLECTION_REFERENCE = "likes"
		private const val USER_SKIPS_COLLECTION_REFERENCE = "skips"
		private const val USER_MATCHES_COLLECTION_REFERENCE = "matches"
		private const val TAG = "mylogs"
	}

	private val currentUserId = currentUser.userId
	private val preferedGender = currentUser.preferedGender

	//note:debug only
	//
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("scmxqiwwci").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("vuwtmiegcl").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("qjfarvjwne").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("sqbnmdaiuy").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("wosfvtydqb").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)



	/*
	* note: swiped left
	*/
	override fun addToSkipped(skipedUser: User) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_SKIPS_COLLECTION_REFERENCE)
			.document(skipedUser.userId)
			.set(skipedUser)
	}
	/*
	* note: swiped right
 	* check if users liked each other
	*/
	override fun handlePossibleMatch(likedUser: User) {
		val likedUserId = likedUser.userId
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(likedUserId)
			.collection(USER_LIKES_COLLECTION_REFERENCE)
			.document(currentUserId)
			.get()
			.addOnSuccessListener { documentSnapshot ->
				if (documentSnapshot.exists()) {
					//add to match collection
					firestore.collection(USERS_COLLECTION_REFERENCE)
						.document(likedUserId)
						.collection(USER_MATCHES_COLLECTION_REFERENCE)
						.document(currentUserId)
						.set(currentUser)
					firestore.collection(USERS_COLLECTION_REFERENCE)
						.document(currentUserId)
						.collection(USER_MATCHES_COLLECTION_REFERENCE)
						.document(likedUserId)
						.set(likedUser)

					//remove from like collection
					//note:uncomment for release
//					mUsersCollectionRef
//							.document(uId)
//							.collection(USER_LIKES_COLLECTION_REFERENCE)
//							.document(mCurrentUserId)
//							.delete();
					firestore.collection(USERS_COLLECTION_REFERENCE)
						.document(currentUserId)
						.collection(USER_LIKES_COLLECTION_REFERENCE)
						.document(likedUserId)
						.delete()
					Log.wtf(TAG, "match handle executed")

				}
				else firestore.collection(USERS_COLLECTION_REFERENCE)
					.document(currentUserId)
					.collection(USER_LIKES_COLLECTION_REFERENCE)
					.document(likedUserId)
					.set(likedUser)

			}.addOnFailureListener { Log.wtf(TAG, "handlePossibleMatch fail") }
	}



	override fun getPotentialUserCards(): Single<List<User>> {
		return Single.create(SingleOnSubscribe<List<User>>{
			emitter -> getAllUsersCards()

		}).subscribeOn(Schedulers.io())
	}

	fun getPotentialUser(){
		val b = getLikedUsersCards()
		val a = getAllUsersCards()
			.map { userList -> userList.filter {user -> user.userId.equals("a") }}




	}


	/*
	* GET ALL USERS
	*/
	private fun getAllUsersCards(): Single<List<User>> {
		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.whereEqualTo(USERS_FILTER, preferedGender)
			//.limit(limit)
			.get()
		return Single.create(SingleOnSubscribe<List<User>>{emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					val allUsersCards = ArrayList<User>()
					for (doc in it.result!!.documents)
						allUsersCards.add(doc.toObject(User::class.java)!!)
					Log.wtf(TAG, "all on complete, size = " + allUsersCards.size)
					emitter.onSuccess(allUsersCards)
				}
			}.addOnFailureListener {
				Log.wtf(TAG, "all fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	/*
	* GET LIKED USERS
	*/
	private fun getLikedUsersCards(): Single<List<String>> {
		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_LIKES_COLLECTION_REFERENCE)
			.get()
		return Single.create(SingleOnSubscribe<List<String>>{ emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					val likedUsersCardsIds = ArrayList<String>()
					for (doc in it.result!!.documents)
						likedUsersCardsIds.add(doc.id)
					Log.wtf(TAG, "likes on complete, size = " + likedUsersCardsIds.size)
					emitter.onSuccess(likedUsersCardsIds)
				}
			}.addOnFailureListener {
				Log.wtf(TAG, "liked fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())

	}

	/*
	* GET MATCHED
	*/
	private fun getMatchedUsersCards(): Single<List<String>> {
		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_MATCHES_COLLECTION_REFERENCE)
			.get()
		return Single.create(SingleOnSubscribe<List<String>> { emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					val matchedUsersCardsIds = ArrayList<String>()
					for (doc in it.result!!.documents)
						matchedUsersCardsIds.add(doc.id)
					Log.wtf(TAG, "matches on complete, size = " + matchedUsersCardsIds.size)
					emitter.onSuccess(matchedUsersCardsIds)
				}
			}.addOnFailureListener {
				Log.wtf(TAG, "matches fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	/*
	* GET SKIPED USERS
	*/
	private fun getSkipedUsersCards(): Single<List<String>> {
		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_SKIPS_COLLECTION_REFERENCE)
			.get()
		return Single.create(SingleOnSubscribe<List<String>> { emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					val skippedUsersCardsIds = ArrayList<String>()
					for (doc in it.result!!.documents)
						skippedUsersCardsIds.add(doc.id)
					Log.wtf(TAG, "skips on complete, size = " + skippedUsersCardsIds.size)
					emitter.onSuccess(skippedUsersCardsIds)
				}
			}.addOnFailureListener {
				emitter.onError(it)
				Log.wtf(TAG, "skipped fail")
			}

		}).subscribeOn(Schedulers.io())


	}

//
//	//merge liked, skiped, matched lists
//	private fun mergeLikedSkipedMatched() {
//		val mergedLikesSkipsCardsIds = ArrayList(mLikedUsersCardsIds)
//		mergedLikesSkipsCardsIds.addAll(mSkipedUsersCardsIds)
//		mergedLikesSkipsCardsIds.addAll(mMatchedUsersCardsIds)
//		if (mergedLikesSkipsCardsIds.size != 0) {
//			val returningProfileModels = ArrayList<User>()
//			for (profileModel in mAllUsersCards)
//				if (!mergedLikesSkipsCardsIds.contains(profileModel.userId)) returningProfileModels.add(profileModel)
//			Log.wtf("logs", "potential users available = " + returningProfileModels.size)
//			potentialUsersCards!!.postValue(returningProfileModels)
//		}
//		else {
//			potentialUsersCards!!.postValue(mAllUsersCards)
//			Log.wtf("logs", "likes + skips + matches = 0 ")
//		}
//
//	}









}