/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.auth

import com.facebook.login.LoginManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.user.entity.UserItem
import com.mmdev.data.user.UserRepositoryLocal
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth,
                                             private val db: FirebaseFirestore,
                                             private val fbLogin: LoginManager,
                                             private val userRepositoryLocal: UserRepositoryLocal): AuthRepository {

	companion object {
		private const val GENERAL_COLLECTION_REFERENCE = "users"
	}

	/**
	 * Observable which track the auth changes of [FirebaseAuth] to listen when an user is logged or not.
	 *
	 * @return an [Observable] which emits every time that the [FirebaseAuth] state change.
	 */
	override fun isAuthenticated(): Observable<Boolean> {
		return Observable.create(ObservableOnSubscribe<Boolean>{ emitter ->
			val authStateListener = FirebaseAuth.AuthStateListener {
				auth -> if (auth.currentUser == null) emitter.onNext(false)
				else emitter.onNext(true)
			}
			auth.addAuthStateListener(authStateListener)
			emitter.setCancellable { auth.removeAuthStateListener(authStateListener) }
		}).subscribeOn(Schedulers.io())
	}


	/**
	 * check is user id already stored in db
	 * @return [UserItem] object which is stored in db and store it locally
	 */
	override fun handleUserExistence(userId: String): Single<UserItem> {
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE).document(userId)
			ref.get().addOnCompleteListener { task ->
				if (task.isSuccessful) {
					val document = task.result
					if (document!!.exists()) {
						val user = document.toObject(UserItem::class.java)
						userRepositoryLocal.saveUserInfo(user!!)
						emitter.onSuccess(user)
					}
					else emitter.onError(Exception("User do not exist"))
				}
				else emitter.onError(Exception("task is not successful"))
			}
		}).subscribeOn(Schedulers.io())
	}


	/**
	 * this fun is called first when user is trying to sign in via facebook
	 * creates a basic [UserItem] object based on public facebook profile
	 */
	override fun signInWithFacebook(token: String): Single<UserItem> {
		val credential = FacebookAuthProvider.getCredential(token)
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->
			auth.signInWithCredential(credential).addOnCompleteListener { task: Task<AuthResult> ->
				if (task.isSuccessful && auth.currentUser != null) {
					val firebaseUser = auth.currentUser!!
					val photoUrl = firebaseUser.photoUrl.toString() + "?height=500"
					val urls = ArrayList<String>()
					urls.add(photoUrl)
					val user = UserItem(name = firebaseUser.displayName!!,
					                    city = "Kyiv",
					                    mainPhotoUrl = photoUrl,
					                    photoURLs = urls,
					                    userId = firebaseUser.uid)

					emitter.onSuccess(user)
				}
				else emitter.onError(task.exception!!)

			}

		}).subscribeOn(Schedulers.io())
	}


	/**
	 * if [UserItem] is not stored in db -> create new document + save locally
	 */
	override fun registerUser(userItem: UserItem): Completable {
		return Completable.create { emitter ->
			val ref = db.collection(GENERAL_COLLECTION_REFERENCE).document(userItem.userId)
			ref.set(userItem)
				.addOnSuccessListener {
					userRepositoryLocal.saveUserInfo(userItem)
					emitter.onComplete()
				}
				.addOnFailureListener { task -> emitter.onError(task) }
		}.subscribeOn(Schedulers.io())
	}

	override fun logOut(){
		auth.signOut()
		fbLogin.logOut()
	}




}


