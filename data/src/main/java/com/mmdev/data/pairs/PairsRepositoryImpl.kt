/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 15:57
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
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PairsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              currentUser: UserItem): PairsRepository {


	private var currentUserDocRef: DocumentReference
	private var currentUserId: String
	private var paginateMatchesQuery: Query

	init {
		currentUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
			.document(currentUser.baseUserInfo.city)
			.collection(currentUser.baseUserInfo.gender)
			.document(currentUser.baseUserInfo.userId)

		currentUserId = currentUser.baseUserInfo.userId

		paginateMatchesQuery = currentUserDocRef
			.collection(USER_MATCHED_COLLECTION_REFERENCE)
			.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
			.orderBy(MATCHED_DATE_FIELD, Query.Direction.DESCENDING)
			.limit(20)

	}

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
		private const val USER_SKIPPED_COLLECTION_REFERENCE = "skipped"

		private const val CONVERSATIONS_COLLECTION_REFERENCE = "conversations"
		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
		private const val MATCHED_DATE_FIELD = "matchedDate"
	}


	private lateinit var paginateLastMatchedLoaded: DocumentSnapshot


	override fun deleteMatchedUser(matchedUserItem: MatchedUserItem): Completable =
		Completable.create{ emitter ->
			val matchedUserDocRef = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(matchedUserItem.userItem.baseUserInfo.city)
				.collection(matchedUserItem.userItem.baseUserInfo.gender)
				.document(matchedUserItem.userItem.baseUserInfo.userId)

			//delete from  match collection
			currentUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(matchedUserItem.userItem.baseUserInfo.userId)
				.delete()

			//delete from  match collection
			matchedUserDocRef
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.delete()

			//add to skipped collection
			currentUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(matchedUserItem.userItem.baseUserInfo.userId)
				.set(mapOf("userId" to matchedUserItem.userItem.baseUserInfo.userId))

			//add to skipped collection
			matchedUserDocRef
				.collection(USER_SKIPPED_COLLECTION_REFERENCE)
				.document(currentUserId)
				.set(mapOf("userId" to currentUserId))

			//delete predefined conversation
			firestore
				.collection(CONVERSATIONS_COLLECTION_REFERENCE)
				.document(matchedUserItem.conversationId)
				.delete()
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }


		}.subscribeOn(Schedulers.io())

	override fun getMatchedUsersList(): Single<List<MatchedUserItem>> {
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
						paginateMatchesQuery =
							paginateMatchesQuery.startAfter(paginateLastMatchedLoaded)
					}
					else emitter.onSuccess(listOf())
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}

}