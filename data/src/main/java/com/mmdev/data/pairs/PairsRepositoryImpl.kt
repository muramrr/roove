/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.pairs

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.schedulers.ExecuteSchedulers
import com.mmdev.data.user.UserWrapper
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleOnSubscribe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class PairsRepositoryImpl @Inject constructor(firestore: FirebaseFirestore,
                                              userWrapper: UserWrapper):
		PairsRepository, BaseRepositoryImpl(firestore, userWrapper) {


	private var initialMatchesQuery: Query = currentUserDocRef
		.collection(USER_MATCHED_COLLECTION_REFERENCE)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
		.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
		.limit(20)


	private lateinit var paginateLastMatchedLoaded: DocumentSnapshot
	private lateinit var paginateMatchesQuery: Query


	override fun getMatchedUsersList(): Single<List<MatchedUserItem>> =
		Single.create(SingleOnSubscribe<List<MatchedUserItem>> { emitter ->
			reInit()
			initialMatchesQuery
				.get()
				.addOnSuccessListener {
					if (!it.isEmpty) {
						val matchesList = mutableListOf<MatchedUserItem>()
						for (doc in it) {
							matchesList.add(doc.toObject(MatchedUserItem::class.java))
						}
						emitter.onSuccess(matchesList)
						//new cursor position
						paginateLastMatchedLoaded = it.documents[it.size() - 1]
						//init pagination query
						paginateMatchesQuery =
							initialMatchesQuery.startAfter(paginateLastMatchedLoaded)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(ExecuteSchedulers.io())

	override fun getMoreMatchedUsersList(): Single<List<MatchedUserItem>> =
		Single.create(SingleOnSubscribe<List<MatchedUserItem>> { emitter ->
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
						paginateMatchesQuery =
							paginateMatchesQuery.startAfter(paginateLastMatchedLoaded)
					}
					else emitter.onSuccess(emptyList())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(ExecuteSchedulers.io())

	override fun reInit() {
		super.reInit()
		initialMatchesQuery = currentUserDocRef
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
			.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
			.limit(20)
	}
}