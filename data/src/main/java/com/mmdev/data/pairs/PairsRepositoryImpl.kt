/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.02.20 15:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.pairs

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.user.UserItem
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PairsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              currentUser: UserItem): PairsRepository {


	private val currentUserInfo = currentUser.baseUserInfo

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"

		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
	}

	override fun getMatchedUsersList(): Single<List<MatchedUserItem>> =
		Single.create(SingleOnSubscribe<List<MatchedUserItem>> { emitter ->
			firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserInfo.city)
				.collection(currentUserInfo.gender)
				.document(currentUserInfo.userId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
				.get()
				.addOnSuccessListener {
					val matchedUsersList = ArrayList<MatchedUserItem>()
					for (doc in it!!) {
						matchedUsersList.add(doc.toObject(MatchedUserItem::class.java))
					}
					emitter.onSuccess(matchedUsersList)
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())

}