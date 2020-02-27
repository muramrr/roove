/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 15:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.auth

import android.util.Log
import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.auth.AuthUserItem
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
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

	override fun signIn(token: String): Single<HashMap<Boolean, BaseUserInfo>> =
		signInWithFacebook(token)
			.flatMap { checkAndRetrieveFullUser(it) }
			.subscribeOn(Schedulers.io())

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
	private fun signInWithFacebook(token: String): Single<BaseUserInfo> =
		Single.create(SingleOnSubscribe<BaseUserInfo> { emitter ->
			val credential = FacebookAuthProvider.getCredential(token)
			auth.signInWithCredential(credential)
				.addOnCompleteListener{
					if (it.isSuccessful && auth.currentUser != null) {
						val firebaseUser = auth.currentUser!!
						val photoUrl = firebaseUser.photoUrl.toString() + "?height=1000"
						val baseUser =
							BaseUserInfo(name = firebaseUser.displayName!!,
							                                                        mainPhotoUrl = photoUrl,
							                                                        userId = firebaseUser.uid)
						emitter.onSuccess(baseUser)

					}
					else emitter.onError(Exception("Facebook login error"))
				}
				.addOnFailureListener { emitter.onError(Exception("Failed to sign in: $it")) }
		}).observeOn(Schedulers.io())

	private fun checkAndRetrieveFullUser(baseUserInfo: BaseUserInfo): Single<HashMap<Boolean, BaseUserInfo>> =
		Single.create(SingleOnSubscribe<HashMap<Boolean, BaseUserInfo>> { emitter ->
			val ref = db.collection(BASE_COLLECTION_REFERENCE).document(baseUserInfo.userId)
			ref.get()
				.addOnSuccessListener { baseUserDoc ->
					//if base info about user exists in db
					if (baseUserDoc.exists()) {
						val userInBase = baseUserDoc.toObject(AuthUserItem::class.java)!!
						Log.wtf("mylogs_AuthRepoImpl", "$userInBase")
						db.collection(GENERAL_COLLECTION_REFERENCE)
							.document(userInBase.baseUserInfo.city)
							.collection(userInBase.baseUserInfo.gender)
							.document(userInBase.baseUserInfo.userId)
							.get()
							.addOnSuccessListener { fullUserDoc ->
								//if full user info exists in db
								if (fullUserDoc.exists()) {
									val retrievedUser = fullUserDoc.toObject(UserItem::class.java)!!
									localRepo.saveUserInfo(retrievedUser)
									emitter.onSuccess(hashMapOf(false to retrievedUser.baseUserInfo))
								}
								//auth user exists but full is not => return continue reg flag "true"
								else emitter.onSuccess(hashMapOf(true to userInBase.baseUserInfo))
							}
							.addOnFailureListener { emitter.onError(it) }
					}
					//if user is not stored => return new UserItem with continue reg flag "true"
					else emitter.onSuccess(hashMapOf(true to baseUserInfo))
				}
				.addOnFailureListener { emitter.onError(it) }
		}).observeOn(Schedulers.io())

}


