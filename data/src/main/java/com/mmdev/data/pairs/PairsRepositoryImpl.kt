/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.01.20 18:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.pairs

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.CardItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.user.UserItem
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PairsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              currentUser: UserItem):
		PairsRepository {


	private val currentUserInfo = currentUser.baseUserInfo

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"

		private const val CONVERSATION_STARTED_FIELD = "conversationStarted"
	}

	override fun getMatchedUsersList(): Observable<List<CardItem>> {

		return Observable.create(ObservableOnSubscribe<List<CardItem>> { emitter ->
			val listener = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserInfo.city)
				.collection(currentUserInfo.gender)
				.document(currentUserInfo.userId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.whereEqualTo(CONVERSATION_STARTED_FIELD, false)
				.addSnapshotListener { snapshots, e ->
					if (e != null) {
						emitter.onError(e)
						return@addSnapshotListener
					}
					val matchedUsersList = ArrayList<CardItem>()
					for (doc in snapshots!!) {
						matchedUsersList.add(doc.toObject(CardItem::class.java))
					}
					emitter.onNext(matchedUsersList)
				}
			emitter.setCancellable { listener.remove() }
		}).subscribeOn(Schedulers.io())
	}


}