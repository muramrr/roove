/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.02.20 18:04
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.pairs

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

class PairsRepositoryImpl @Inject constructor(firestore: FirebaseFirestore,
                                              currentUser: UserItem): PairsRepository {


	private var currentUserDocRef: DocumentReference
	init {
		currentUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)
	}

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"

		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		private const val MATCHED_DATE_FIELD = "matchedDate"
	}

	private lateinit var paginateMatchesQuery: Query
	private lateinit var paginateLastMatchedLoaded: DocumentSnapshot


	override fun getMatchedUsersList(): Single<List<MatchedUserItem>> {
		//check is this first call
		if (!this::paginateMatchesQuery.isInitialized)
			paginateMatchesQuery = currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
				.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
				.limit(20)

		return Single.create(SingleOnSubscribe<List<MatchedUserItem>> { emitter ->
			paginateMatchesQuery
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val paginateMatchesList = ArrayList<MatchedUserItem>()
						for (doc in it) {
							paginateMatchesList.add(doc.toObject(MatchedUserItem::class.java))
						}
						emitter.onSuccess(paginateMatchesList)
						//new cursor position
						paginateLastMatchedLoaded = it.documents[it.size() - 1]
						//update query with new cursor position
						paginateMatchesQuery = currentUserDocRef
							.collection(USER_MATCHED_COLLECTION_REFERENCE)
							.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
							.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
							.limit(20)
							.startAfter(paginateLastMatchedLoaded)
					}
				}.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}

}