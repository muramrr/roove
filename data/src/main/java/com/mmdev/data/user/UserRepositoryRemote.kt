/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.user.entity.UserItem
import com.mmdev.business.user.repository.UserRepository
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
class UserRepositoryRemote @Inject constructor(private val firestore: FirebaseFirestore):
		UserRepository.RemoteUserRepository {

	companion object {
		private const val GENERAL_COLLECTION_REFERENCE = "users"
	}

	override fun createUserOnRemote(): Completable {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun deleteUser(userId: String): Completable {
		return Completable.create { emitter ->
			val ref = firestore
				.collection(GENERAL_COLLECTION_REFERENCE)
				.document(userId)
			ref.delete()
				.addOnSuccessListener {
					emitter.onComplete()
				}
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(Schedulers.io())
	}

	override fun getUserById(userId: String): Single<UserItem> {
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->
			firestore.collection(GENERAL_COLLECTION_REFERENCE)
				.document(userId)
				.get()
				.addOnSuccessListener {
					if (it.exists() && it != null)
						emitter.onSuccess(it.toObject(UserItem::class.java)!!)
					else emitter.onError(Exception("User doesn't exist"))
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}
}