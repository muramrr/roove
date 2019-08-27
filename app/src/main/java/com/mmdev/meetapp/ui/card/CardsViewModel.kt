package com.mmdev.meetapp.ui.card

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.domain.user.model.User
import java.util.*

/* Created by A on 20.07.2019.*/

/**
 * get users from firebase firestore
 * getAllUsers -> getSkipedUsers -> getLikedUsers -> getMatchedUsers -> mergeLikedSkipedMatched ->
 * -> create new list from getAllUsersCards list that does not contains mergedLikedSkipedMatched items
 * -> postValue into LiveData variable ... else return null and show loading bar in Fragment class
 * TODO: MAKE ASYNC DATA RETRIEVE AND FETCH DYNAMICALLY
 * TODO: convert likes -> matches on other user side
 */

class CardsViewModel: ViewModel() {

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USERS_FILTER = "gender"
		private const val USER_LIKES_COLLECTION_REFERENCE = "likes"
		private const val USER_SKIPS_COLLECTION_REFERENCE = "skips"
		private const val USER_MATCHES_COLLECTION_REFERENCE = "matches"
	}

	private lateinit var mCurrentUser: User
	private var mCurrentUserId: String? = ""

	private var mUsersCollectionRef: CollectionReference? = null
	private var mCurrentProfileDocRef: DocumentReference? = null

	private val mAllUsersCards = ArrayList<User>()
	private val mSkipedUsersCardsIds = ArrayList<String>()
	private val mLikedUsersCardsIds = ArrayList<String>()
	private val mMatchedUsersCardsIds = ArrayList<String>()

	internal var matchedUser: MutableLiveData<User>? = MutableLiveData()

	internal var potentialUsersCards: MutableLiveData<List<User>>? = MutableLiveData()

	internal fun init(currentUser: User) {
		mCurrentUser = currentUser
		mCurrentUserId = mCurrentUser.userId
		val firestore = FirebaseFirestore.getInstance()
		mUsersCollectionRef = firestore.collection(USERS_COLLECTION_REFERENCE)
		mCurrentProfileDocRef = mUsersCollectionRef!!.document(mCurrentUserId!!)
		loadUsers(mCurrentUser.preferedGender)
		//TODO:debug only
		//
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("scmxqiwwci").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("vuwtmiegcl").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("qjfarvjwne").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("sqbnmdaiuy").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
//		firestore.collection(USERS_COLLECTION_REFERENCE).document("wosfvtydqb").collection(USER_LIKES_COLLECTION_REFERENCE).document(mCurrentUserId!!).set(mCurrentUser!!)
	}



	private fun loadUsers(preferedGender: String?) { getAllUsersCards(preferedGender!!)
		//potentialUsersCards.postValue(returningModels);
	}


	/*
	* GET ALL USERS
	*/
	private fun getAllUsersCards(preferedGender: String) {
		mUsersCollectionRef!!
			.whereEqualTo(USERS_FILTER, preferedGender)
			//.limit(limit)
			.get()
			.addOnCompleteListener { task ->
				if (task.result != null) {
					for (doc in task.result!!.documents) mAllUsersCards.add(doc.toObject(User::class.java)!!)
					getSkipedUsersCards()
					Log.wtf("logs", "all on complete, size = " + mAllUsersCards.size)
				}
			}
			.addOnFailureListener { Log.wtf("logs", "all fail") }
	}

	/*
	 * GET SKIPED USERS
	 */
	private fun getSkipedUsersCards() {
		mCurrentProfileDocRef!!
			.collection(USER_SKIPS_COLLECTION_REFERENCE)
			.get()
			.addOnCompleteListener { task ->
				if (task.result != null) {
					for (doc in task.result!!.documents) mSkipedUsersCardsIds.add(doc.id)
					getLikedUsersCards()
					Log.wtf("logs", "skips on complete, size = " + mSkipedUsersCardsIds.size)
				}
			}
			.addOnFailureListener { Log.wtf("logs", "skipped fail") }


	}

	/*
	 * GET LIKED USERS
	 */
	private fun getLikedUsersCards() {
		mCurrentProfileDocRef!!
			.collection(USER_LIKES_COLLECTION_REFERENCE)
			.get()
			.addOnCompleteListener { task ->
				if (task.result != null) {
					for (doc in task.result!!.documents) mLikedUsersCardsIds.add(doc.id)

					Log.wtf("logs", "likes on complete, size = " + mLikedUsersCardsIds.size)
					getMatchedUsersCards()
				}
			}
			.addOnFailureListener { Log.wtf("logs", "liked fail") }

	}

	/*
	 * GET MATCHED
	 */
	private fun getMatchedUsersCards() {
		mCurrentProfileDocRef!!.collection(USER_MATCHES_COLLECTION_REFERENCE).get().addOnCompleteListener { task ->
				if (task.result != null) {
					for (doc in task.result!!.documents) mMatchedUsersCardsIds.add(doc.id)

					Log.wtf("logs", "matches on complete, size = " + mMatchedUsersCardsIds.size)
					mergeLikedSkipedMatched()
				}
			}
			.addOnFailureListener { Log.wtf("logs", "matches fail") }

	}

	//merge liked, skiped, matched lists
	private fun mergeLikedSkipedMatched() {
		val mergedLikesSkipsCardsIds = ArrayList(mLikedUsersCardsIds)
		mergedLikesSkipsCardsIds.addAll(mSkipedUsersCardsIds)
		mergedLikesSkipsCardsIds.addAll(mMatchedUsersCardsIds)
		if (mergedLikesSkipsCardsIds.size != 0) {
			val returningProfileModels = ArrayList<User>()
			for (profileModel in mAllUsersCards)
				if (!mergedLikesSkipsCardsIds.contains(profileModel.userId)) returningProfileModels.add(profileModel)
			Log.wtf("logs", "potential users available = " + returningProfileModels.size)
			potentialUsersCards!!.postValue(returningProfileModels)
		}
		else {
			potentialUsersCards!!.postValue(mAllUsersCards)
			Log.wtf("logs", "likes + skips + matches = 0 ")
		}

	}

	/*
	* check if users liked each other
	*/
	internal fun handlePossibleMatch(likedUser: User) {
		val uId: String = likedUser.userId
		mUsersCollectionRef!!
			.document(uId)
			.collection(USER_LIKES_COLLECTION_REFERENCE)
			.document(mCurrentUserId!!)
			.get().addOnSuccessListener { documentSnapshot ->
				if (documentSnapshot.exists()) {
					//add to match collection
					mUsersCollectionRef!!
						.document(uId)
						.collection(USER_MATCHES_COLLECTION_REFERENCE)
						.document(mCurrentUserId!!)
						.set(mCurrentUser)
					mCurrentProfileDocRef!!
						.collection(USER_MATCHES_COLLECTION_REFERENCE)
						.document(uId)
						.set(likedUser)
					matchedUser!!.value = likedUser
					//remove from like collection
					//TODO:uncomment for release
//					mUsersCollectionRef
//							.document(uId)
//							.collection(USER_LIKES_COLLECTION_REFERENCE)
//							.document(mCurrentUserId)
//							.delete();
					mCurrentProfileDocRef!!
						.collection(USER_LIKES_COLLECTION_REFERENCE)
						.document(uId)
						.delete()
					Log.wtf("logs", "match handle executed")

				}
				else mUsersCollectionRef!!
					.document(mCurrentUserId!!)
					.collection(USER_LIKES_COLLECTION_REFERENCE)
					.document(uId)
					.set(likedUser)

			}
			.addOnFailureListener { Log.wtf("logs", "handlePossibleMatch fail") }


	}

	internal fun addToSkipped(skipedUser: User) {
		mCurrentProfileDocRef!!
			.collection(USER_SKIPS_COLLECTION_REFERENCE)
			.document(skipedUser.userId)
			.set(skipedUser)
	}




}

