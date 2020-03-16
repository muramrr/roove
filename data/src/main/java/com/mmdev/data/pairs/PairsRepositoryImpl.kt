/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.03.20 15:39
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
import com.mmdev.data.user.UserWrapper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class PairsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              userWrapper: UserWrapper):
		PairsRepository, BaseRepositoryImpl(firestore, userWrapper) {


	private var initialMatchesQuery: Query = currentUserDocRef
		.collection(USER_MATCHED_COLLECTION_REFERENCE)
		.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
		.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
		.limit(20)


	private lateinit var paginateLastMatchedLoaded: DocumentSnapshot
	private lateinit var paginateMatchesQuery: Query


	override fun deleteMatchedUser(matchedUserItem: MatchedUserItem): Completable =
		Completable.create { emitter ->
			reInit()
			val matchedUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(matchedUserItem.baseUserInfo.city)
				.collection(matchedUserItem.baseUserInfo.gender)
				.document(matchedUserItem.baseUserInfo.userId)

			//delete from  match collection
			currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(matchedUserItem.baseUserInfo.userId)
				.delete()

			//delete from  match collection
			matchedUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.delete()

			//add to skipped collection
			currentUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(matchedUserItem.baseUserInfo.userId)
				.set(mapOf(USER_ID_FIELD to matchedUserItem.baseUserInfo.userId))

			//add to skipped collection
			matchedUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.set(mapOf(USER_ID_FIELD to currentUserId))

			//delete predefined conversation
			firestore
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(matchedUserItem.conversationId)
				.delete()
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }


		}.subscribeOn(Schedulers.io())

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
		}).subscribeOn(Schedulers.io())

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
		}).subscribeOn(Schedulers.io())

	override fun reInit() {
		super.reInit()
		initialMatchesQuery = currentUserDocRef
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
			.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
			.limit(20)
	}
}