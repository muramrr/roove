package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.domain.cards.repository.CardsRepository
import com.mmdev.domain.core.model.User
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/* Created by A on 14.09.2019.*/

/**
 * This is the documentation block about the class
 */
@Singleton
class CardsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              private val currentUser: User):
		CardsRepository {

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USERS_FILTER = "gender"
		private const val USER_LIKES_COLLECTION_REFERENCE = "likes"
		private const val USER_SKIPS_COLLECTION_REFERENCE = "skips"
		private const val USER_MATCHES_COLLECTION_REFERENCE = "matches"
		private const val TAG = "mylogs"
	}


	//note:debug only
	//
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("scmxqiwwci").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("vuwtmiegcl").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("qjfarvjwne").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("sqbnmdaiuy").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	//		firestore.collection(USERS_COLLECTION_REFERENCE).document("wosfvtydqb").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)

	/*
	* note: swiped right
 	* check if users liked each other
	*/
	override fun handlePossibleMatch(likedUser: User) {
		val uId = likedUser.userId
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(uId)
			.collection(USER_LIKES_COLLECTION_REFERENCE)
			.document(currentUser.userId)
			.get()
			.addOnSuccessListener { documentSnapshot ->
				if (documentSnapshot.exists()) {
					//add to match collection
					firestore.collection(USERS_COLLECTION_REFERENCE)
						.document(uId)
						.collection(USER_MATCHES_COLLECTION_REFERENCE)
						.document(currentUser.userId)
						.set(currentUser)
					firestore.collection(USERS_COLLECTION_REFERENCE)
						.document(currentUser.userId)
						.collection(USER_MATCHES_COLLECTION_REFERENCE)
						.document(uId)
						.set(likedUser)

					//remove from like collection
					//note:uncomment for release
//					mUsersCollectionRef
//							.document(uId)
//							.collection(USER_LIKES_COLLECTION_REFERENCE)
//							.document(mCurrentUserId)
//							.delete();
					firestore.collection(USERS_COLLECTION_REFERENCE)
						.document(currentUser.userId)
						.collection(USER_LIKES_COLLECTION_REFERENCE)
						.document(uId)
						.delete()
					Log.wtf(TAG, "match handle executed")

				}
				else firestore.collection(USERS_COLLECTION_REFERENCE)
					.document(currentUser.userId)
					.collection(USER_LIKES_COLLECTION_REFERENCE)
					.document(uId)
					.set(likedUser)

			}.addOnFailureListener { Log.wtf(TAG, "handlePossibleMatch fail") }
	}

	/*
	 * note: swiped left
	 */
	override fun addToSkipped(skipedUser: User) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.userId)
			.collection(USER_SKIPS_COLLECTION_REFERENCE)
			.document(skipedUser.userId)
			.set(skipedUser)
	}

	/*
	* GET ALL USERS
	*/
	private fun getAllUsersCards(preferedGender: String) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.whereEqualTo(USERS_FILTER, preferedGender)
			//.limit(limit)
			.get()
			.addOnCompleteListener {
				if (it.result != null) {
					val mAllUsersCards = ArrayList<User>()
					for (doc in it.result!!.documents)
						mAllUsersCards.add(doc.toObject(User::class.java)!!)
					Log.wtf(TAG, "all on complete, size = " + mAllUsersCards.size)
				}
			}
			.addOnFailureListener { Log.wtf(TAG, "all fail") }
	}

	/*
	 * GET SKIPED USERS
	 */
	private fun getSkipedUsersCards() {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.userId)
			.collection(USER_SKIPS_COLLECTION_REFERENCE)
			.get()
			.addOnCompleteListener {
				if (it.result != null) {
					val mSkipedUsersCardsIds = ArrayList<String>()
					for (doc in it.result!!.documents)
						mSkipedUsersCardsIds.add(doc.id)
					Log.wtf(TAG, "skips on complete, size = " + mSkipedUsersCardsIds.size)
				}
			}
			.addOnFailureListener { Log.wtf(TAG, "skipped fail") }


	}

	/*
	 * GET LIKED USERS
	 */
	private fun getLikedUsersCards() {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.userId)
			.collection(USER_LIKES_COLLECTION_REFERENCE)
			.get()
			.addOnCompleteListener {
				if (it.result != null) {
					val mLikedUsersCardsIds = ArrayList<String>()
					for (doc in it.result!!.documents)
						mLikedUsersCardsIds.add(doc.id)
					Log.wtf(TAG, "likes on complete, size = " + mLikedUsersCardsIds.size)
				}
			}
			.addOnFailureListener { Log.wtf(TAG, "liked fail") }

	}

	/*
	 * GET MATCHED
	 */
	private fun getMatchedUsersCards() {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.userId)
			.collection(USER_MATCHES_COLLECTION_REFERENCE)
			.get()
			.addOnCompleteListener {
				if (it.result != null) {
					val mMatchedUsersCardsIds = ArrayList<String>()
					for (doc in it.result!!.documents)
						mMatchedUsersCardsIds.add(doc.id)
					Log.wtf(TAG, "matches on complete, size = " + mMatchedUsersCardsIds.size)
				}
			}
			.addOnFailureListener { Log.wtf(TAG, "matches fail") }

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