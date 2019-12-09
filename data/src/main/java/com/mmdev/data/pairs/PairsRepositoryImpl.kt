/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.pairs

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.cards.entity.CardItem
import com.mmdev.business.pairs.PairsRepository
import com.mmdev.business.user.entity.UserItem
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class PairsRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore,
                                              currentUserItem: UserItem):
		PairsRepository {


	private val currentUserId = currentUserItem.userId

	companion object {
		private const val USERS_COLLECTION_REFERENCE = "users"
		private const val USER_MATCHED_COLLECTION_REFERENCE = "matched"
	}

	override fun getMatchedUsersList(): Observable<List<CardItem>> {

		return Observable.create(ObservableOnSubscribe<List<CardItem>> { emitter ->
			val listener = firestore.collection(USERS_COLLECTION_REFERENCE)
				.document(currentUserId)
				.collection(USER_MATCHED_COLLECTION_REFERENCE)
				.whereEqualTo("conversationStarted", false)
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