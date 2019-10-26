package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.user.model.UserItem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
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
		private const val USERS_FILTER = "gender"
		private const val USER_LIKES_COLLECTION_REFERENCE = "likes"
		private const val USER_SKIPS_COLLECTION_REFERENCE = "skips"
		private const val USER_MATCHES_COLLECTION_REFERENCE = "matches"
		private const val TAG = "mylogs"
	}

	private val currentUserId = currentUserItem.userId
	private val preferedGender = currentUserItem.preferedGender

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
	override fun addToSkipped(skippedUserItem: UserItem) {
		firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUserId)
			.collection(USER_SKIPS_COLLECTION_REFERENCE)
			.document(skippedUserItem.userId)
			.set(skippedUserItem)
	}
	/*
	* note: swiped right
 	* check if users liked each other
	*/
	override fun handlePossibleMatch(likedUserItem: UserItem): Single<Boolean> {
		val likedUserId = likedUserItem.userId
		return Single.create(SingleOnSubscribe<Boolean> { emitter ->

			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(likedUserId)
				.collection(USER_LIKES_COLLECTION_REFERENCE)
				.document(currentUserId)
				.get()
				.addOnSuccessListener{ documentSnapshot ->
					if (documentSnapshot.exists()) {
						//add to match collection
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(likedUserId)
							.collection(USER_MATCHES_COLLECTION_REFERENCE)
							.document(currentUserId)
							.set(currentUserItem)
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(USER_MATCHES_COLLECTION_REFERENCE)
							.document(likedUserId)
							.set(likedUserItem)

						//remove from like collection
						//note:uncomment for release
		//					firestore.collection(USERS_COLLECTION_REFERENCE)
		//							.document(likedUserId)
		//							.collection(USER_LIKES_COLLECTION_REFERENCE)
		//							.document(mCurrentUserId)
		//							.delete();
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(USER_LIKES_COLLECTION_REFERENCE)
							.document(likedUserId)
							.delete()
						Log.wtf(TAG, "match handle executed")

						emitter.onSuccess(true)
					}

					else {
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(currentUserId)
							.collection(USER_LIKES_COLLECTION_REFERENCE)
							.document(likedUserId)
							.set(likedUserItem)
						emitter.onSuccess(false)
					}

			}.addOnFailureListener {
				Log.wtf(TAG, "handlePossibleMatch fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	/* return filtered users list as Single */
	override fun getPotentialUserCards(): Single<List<UserItem>> {
		return Single.zip(getAllUsersCards(),
		                  zipLists(),
		                  BiFunction<List<UserItem>, List<String>, List<UserItem>>
		                  { userList, ids  -> filterUsers(userList, ids) })
			.observeOn(Schedulers.io())

	}

	/* return filtered all users list from already written ids as List<UserItem> */
	private fun filterUsers(userItemList: List<UserItem>, ids: List<String>): List<UserItem>{
		val filteredUsersList = ArrayList<UserItem>()
		for (user in userItemList)
			if (!ids.contains(user.userId))
				filteredUsersList.add(user)
		return filteredUsersList
	}


	/* return merged lists as Single */
	private fun zipLists(): Single<List<String>>{
		return Single.zip(getLikedUsersCards(),
		                  getMatchedUsersCards(),
		                  getSkippedUsersCards(),
		                  Function3<List<String>, List<String>, List<String>, List<String>>
		                  { likes, matches, skipped -> mergeLists(likes, matches, skipped) })
			.subscribeOn(Schedulers.io())
	}

	/* merge all liked + matched + skipped users lists */
	private fun mergeLists(likes:List<String>,
	                       matches:List<String>,
	                       skipped: List<String>): List<String>{
		val uidList = ArrayList<String>()
		uidList.addAll(likes)
		uidList.addAll(matches)
		uidList.addAll(skipped)
		return uidList
	}

	/*
	* GET ALL USERS OBJECTS
	*/
	private fun getAllUsersCards(): Single<List<UserItem>> {
		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.whereEqualTo(USERS_FILTER, preferedGender)
			//.limit(limit)
			.get()
		return Single.create(SingleOnSubscribe<List<UserItem>>{ emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					val allUsersCards = ArrayList<UserItem>()
					for (doc in it.result!!.documents)
						allUsersCards.add(doc.toObject(UserItem::class.java)!!)
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
	* GET LIKED USERS IDS LIST
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
				Log.wtf(TAG, "liked fail + $it")
				emitter.onError(it)
			}
		}).observeOn(Schedulers.io())

	}

	/*
	* GET MATCHED IDS LIST
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
				Log.wtf(TAG, "matches fail + $it")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}

	/*
	* GET SKIPPED USERS IDS LIST
	*/
	private fun getSkippedUsersCards(): Single<List<String>> {
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
				Log.wtf(TAG, "skipped fail + $it")
			}

		}).subscribeOn(Schedulers.io())


	}

}