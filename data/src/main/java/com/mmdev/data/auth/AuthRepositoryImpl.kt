/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 21.12.19 20:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.auth

import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.data.user.UserRepositoryLocal
import com.mmdev.data.user.UserRepositoryRemoteImpl
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth,
                                             private val db: FirebaseFirestore,
                                             private val fbLogin: LoginManager,
                                             private val localRepo: UserRepositoryLocal,
                                             private val remoteRepo: UserRepositoryRemoteImpl): AuthRepository {

	companion object {
		private const val GENERAL_COLLECTION_REFERENCE = "users"
		private const val BASE_COLLECTION_REFERENCE = "usersBase"
	}

	/**
	 * Observable which track the auth changes of [FirebaseAuth] to listen when an user is logged or not.
	 *
	 * @return an [Observable] which emits every time that the [FirebaseAuth] state change.
	 */
	override fun isAuthenticatedListener(): Observable<Boolean> {
		return Observable.create(ObservableOnSubscribe<Boolean>{ emitter ->
			val authStateListener = FirebaseAuth.AuthStateListener {
				auth -> if (auth.currentUser == null) emitter.onNext(false)
				else emitter.onNext(true)
			}
			auth.addAuthStateListener(authStateListener)
			emitter.setCancellable { auth.removeAuthStateListener(authStateListener) }
		}).subscribeOn(Schedulers.io())
	}

	override fun signIn(token: String): Single<UserItem>{
		return signInWithFacebook(token)
			.flatMap { checkIfRegistered(it) }
			.subscribeOn(Schedulers.io())
	}

	/**
	 * create new [UserItem] document on remote
	 */
	override fun registerUser(userItem: UserItem): Completable =
		remoteRepo.createUserOnRemote(userItem)
			.doOnComplete { localRepo.saveUserInfo(userItem) }
			.observeOn(Schedulers.io())



	override fun logOut(){
		auth.signOut()
		fbLogin.logOut()
	}




	/**
	 * this fun is called first when user is trying to sign in via facebook
	 * creates a basic [BaseUserInfo] object based on public facebook profile
	 */
	private fun signInWithFacebook(token: String): Single<BaseUserInfo> {
		return Single.create(SingleOnSubscribe<BaseUserInfo> { emitter ->
			val credential = FacebookAuthProvider.getCredential(token)

			auth.signInWithCredential(credential)
				.addOnSuccessListener {
					if (it.user != null) {
						val firebaseUser = it.user!!
						val photoUrl = firebaseUser.photoUrl.toString() + "?height=500"
						val baseUser = BaseUserInfo(name = firebaseUser.displayName!!,
						                            mainPhotoUrl = photoUrl,
						                            userId = firebaseUser.uid)
						emitter.onSuccess(baseUser)

					}
					else emitter.onError(Exception("User is null"))
				}
				.addOnFailureListener { emitter.onError(Exception("Failed to sign in: $it")) }

		}).subscribeOn(Schedulers.io())
	}


	/**
	 * checks if there is already store such user with this uId
	 * @return [UserItem]
	 */
	private fun checkIfRegistered(baseUserInfo: BaseUserInfo): Single<UserItem> {
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->
			val ref = db.collection(BASE_COLLECTION_REFERENCE)
			ref.document(baseUserInfo.userId).get()
				.addOnSuccessListener { userDoc ->
					if (userDoc.exists()) {
						val userInBase = userDoc.toObject(BaseUserInfo::class.java)!!
						db.collection(GENERAL_COLLECTION_REFERENCE)
							.document(userInBase.city)
							.collection(userInBase.gender)
							.document(userInBase.userId)
							.get()
							.addOnSuccessListener {
								if (it.exists()) emitter.onSuccess(it.toObject(UserItem::class.java)!!)
								else emitter.onSuccess(UserItem(userInBase))
							}
							.addOnFailureListener { emitter.onError(it) }
					} else emitter.onSuccess(UserItem(baseUserInfo))
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(Schedulers.io())
	}


}


