/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.12.19 18:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.business.user.repository.RemoteUserRepository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.SingleOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 * Remote FireBase structure
 * users -> city -> gender -> userDocument
 */

@Singleton
class UserRepositoryRemoteImpl @Inject constructor(private val db: FirebaseFirestore):
		RemoteUserRepository {

	companion object {
		private const val GENERAL_COLLECTION_REFERENCE = "users"
	}

	override fun createUserOnRemote(userItem: UserItem): Completable {
		return Completable.create { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE)
				.document(userItem.baseUserInfo.city)
				.collection(userItem.baseUserInfo.gender)
				.document(userItem.baseUserInfo.userId)
			ref.set(userItem)
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { task -> emitter.onError(task) }
		}.subscribeOn(Schedulers.io())
	}

	override fun deleteUser(userItem: UserItem): Completable {
		return Completable.create { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE)
				.document(userItem.baseUserInfo.city)
				.collection(userItem.baseUserInfo.gender)
				.document(userItem.baseUserInfo.userId)
			ref.delete()
				.addOnSuccessListener { emitter.onComplete() }
				.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(Schedulers.io())
	}

	override fun fetchUserInfo(userItem: UserItem): Single<UserItem> {
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE)
				.document(userItem.baseUserInfo.city)
				.collection(userItem.baseUserInfo.gender)
				.document(userItem.baseUserInfo.userId)
			ref.get()
				.addOnSuccessListener {
					val remoteUserItem = it.toObject(UserItem::class.java)!!
					if (userItem == remoteUserItem) emitter.onSuccess(userItem)
					else emitter.onSuccess(remoteUserItem)
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}

	override fun getFullUserInfo(baseUserInfo: BaseUserInfo): Single<UserItem> {
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE)
				.document(baseUserInfo.city)
				.collection(baseUserInfo.gender)
				.document(baseUserInfo.userId)
			ref.get()
				.addOnSuccessListener { emitter.onSuccess(it.toObject(UserItem::class.java)!!) }
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}

}