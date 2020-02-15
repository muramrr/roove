/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.02.20 14:20
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
import com.mmdev.business.auth.AuthUserItem
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.user.BaseUserInfo
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
			val authStateListener = FirebaseAuth.AuthStateListener { auth ->
				if (auth.currentUser == null) emitter.onNext(false)
				else {
					val ref = db.collection(BASE_COLLECTION_REFERENCE)
					ref.document(auth.currentUser!!.uid)
						.get()
						.addOnSuccessListener { userDoc -> emitter.onNext(userDoc.exists()) }
						.addOnFailureListener { emitter.onNext(false) }
				}
				//Log.wtf("mylogs_AuthRepoImpl", "${auth.currentUser}")
			}
			auth.addAuthStateListener(authStateListener)
			emitter.setCancellable { auth.removeAuthStateListener(authStateListener) }
		}).subscribeOn(Schedulers.io())
	}

	override fun signIn(token: String): Single<UserItem>{
		return signInWithFacebook(token)
			.flatMap { retrieveFullUser(it) }
			.subscribeOn(Schedulers.io())
	}

	/**
	 * create new [UserItem] document on remote
	 */
	override fun registerUser(userItem: UserItem): Completable =
		remoteRepo.createUserOnRemote(userItem)
			.doOnComplete { localRepo.saveUserInfo(userItem) }
			.subscribeOn(Schedulers.io())



	override fun logOut(){
		if (auth.currentUser != null) {
			auth.signOut()
			fbLogin.logOut()
		}
	}

	/**
	 * this fun is called first when user is trying to sign in via facebook
	 * creates a basic [BaseUserInfo] object based on public facebook profile
	 */
	private fun signInWithFacebook(token: String): Single<BaseUserInfo> {
		val credential = FacebookAuthProvider.getCredential(token)
		return Single.create(SingleOnSubscribe<BaseUserInfo> { emitter ->

			auth.signInWithCredential(credential)
				.addOnCompleteListener{
					if (it.isSuccessful && auth.currentUser != null) {
						val firebaseUser = auth.currentUser!!
						val photoUrl = firebaseUser.photoUrl.toString() + "?height=500"
						val baseUser = BaseUserInfo(name = firebaseUser.displayName!!,
						                            mainPhotoUrl = photoUrl,
						                            userId = firebaseUser.uid)
						emitter.onSuccess(baseUser)

					}
					else emitter.onError(Exception("Facebook login error"))
				}
				.addOnFailureListener { emitter.onError(Exception("Failed to sign in: $it")) }

		}).observeOn(Schedulers.io())
	}

	private fun retrieveFullUser(baseUserInfo: BaseUserInfo): Single<UserItem> {
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->
			val ref = db.collection(BASE_COLLECTION_REFERENCE).document(baseUserInfo.userId)
			ref.get()
				.addOnSuccessListener { userDoc ->
					if (userDoc.exists()) {
						val userInAuthBase = userDoc.toObject(AuthUserItem::class.java)!!
						db.collection(GENERAL_COLLECTION_REFERENCE)
							.document(userInAuthBase.baseUserInfo.city)
							.collection(userInAuthBase.baseUserInfo.gender)
							.document(userInAuthBase.baseUserInfo.userId)
							.get()
							.addOnSuccessListener {
								if (it.exists()) {
									val retrievedUser = it.toObject(UserItem::class.java)!!
									localRepo.saveUserInfo(retrievedUser)
									emitter.onSuccess(retrievedUser)
								}
								else emitter.onSuccess(UserItem(userInAuthBase.baseUserInfo))
							}
							.addOnFailureListener { emitter.onError(it) }
					} else emitter.onSuccess(UserItem(baseUserInfo))
				}
				.addOnFailureListener { emitter.onError(it) }
		}).observeOn(Schedulers.io())
	}

}


