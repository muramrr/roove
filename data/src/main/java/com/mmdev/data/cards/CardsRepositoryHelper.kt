/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.cards

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.user.model.UserItem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

/**
 * This is the documentation block about the class
 */

class CardsRepositoryHelper constructor(private val firestore: FirebaseFirestore,
                                        private val currentUser: UserItem) {


	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USERS_FILTER = "gender"
		private const val USER_LIKED_COLLECTION_REFERENCE = "liked"
		private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		private const val TAG = "mylogs"
	}


	/*
	* GET ALL USERS OBJECTS
	*/
	fun getAllUsersCards(): Single<List<CardItem>> {
		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.whereEqualTo(USERS_FILTER, currentUser.preferedGender)
			//.limit(limit)
			.get()
		return Single.create(SingleOnSubscribe<List<CardItem>>{ emitter ->
			query.addOnCompleteListener {
				if (it.result != null) {
					val allUsersCards = ArrayList<CardItem>()
					for (doc in it.result!!.documents)
						allUsersCards.add(CardItem(doc.getString("name")!!,
						                           doc.getString("mainPhotoUrl")!!,
						                           doc.getString("userId")!!))
					Log.wtf(TAG, "all on complete, size = " + allUsersCards.size)
					emitter.onSuccess(allUsersCards)
				}
			}.addOnFailureListener {
				Log.wtf(TAG, "all fail")
				emitter.onError(it)
			}

		}).subscribeOn(Schedulers.io())
	}


	/* return merged lists as Single */
	fun zipLists(): Single<List<String>>{
		return Single.zip(getLikedUsersCards(),
		                  getMatchedUsersCards(),
		                  getSkippedUsersCards(),
		                  Function3<List<String>, List<String>, List<String>, List<String>>
		                  { likes, matches, skipped -> mergeLists(likes, matches, skipped) })
			.subscribeOn(Schedulers.io())
	}

	/* merge all liked + matched + skipped users lists */
	private fun mergeLists(liked:List<String>,
	                       matched:List<String>,
	                       skipped: List<String>): List<String>{
		val uidList = ArrayList<String>()
		uidList.addAll(liked)
		uidList.addAll(matched)
		uidList.addAll(skipped)
		return uidList
	}

	/*
	* GET LIKED USERS IDS LIST
	*/
	private fun getLikedUsersCards(): Single<List<String>> {

		val query = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.userId)
			.collection(USER_LIKED_COLLECTION_REFERENCE)
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
			.document(currentUser.userId)
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
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
			.document(currentUser.userId)
			.collection(USER_SKIPPED_COLLECTION_REFERENCE)
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