/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.repository.auth

import com.facebook.login.LoginManager
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.mmdev.business.auth.AuthRepository
import com.mmdev.business.auth.AuthUserItem
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.ExecuteSchedulers
import com.mmdev.data.repository.user.UserWrapper
import io.reactivex.rxjava3.core.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * login chain: signInWithFacebook => check if exist registered user with same uId =>
 * handle registration => after all complete fetch last results
 * fetchUserInfo() always called last no matter is user just registered or not
 *
 */

@Singleton
class AuthRepositoryImpl @Inject constructor(private val auth: FirebaseAuth,
                                             private val fInstance: FirebaseInstanceId,
                                             private val firestore: FirebaseFirestore,
                                             private val fbLogin: LoginManager,
                                             private val userWrapper: UserWrapper
):
		AuthRepository, BaseRepositoryImpl(firestore, userWrapper) {


	companion object {
		private const val USER_BASE_REGISTRATION_TOKENS_FIELD = "registrationTokens"
	}

	private lateinit var authUserItem: AuthUserItem

	/**
	 * Observable which track the auth changes of [FirebaseAuth] to listen when an user is logged or not.
	 *
	 * @return an [Observable] which emits every time that the [FirebaseAuth] state change.
	 */
	override fun isAuthenticatedListener(): Observable<Boolean> {
		return Observable.create(ObservableOnSubscribe<Boolean>{ emitter ->
			val authStateListener = FirebaseAuth.AuthStateListener { auth ->
				if (auth.currentUser == null) {
					emitter.onNext(false)
					//Log.wtf(TAG, "current user is null +false")
				}
				else if (auth.currentUser != null) {
					//Log.wtf(TAG, "current user is not null")
					val ref = firestore.collection(USERS_BASE_COLLECTION_REFERENCE)
						.document(auth.currentUser!!.uid)
					ref
						.get()
						.addOnSuccessListener {
							//Log.wtf(TAG, "success get document")
							if (it.exists()){
								emitter.onNext(true)
								currentUserId = auth.currentUser!!.uid
								authUserItem = it.toObject(AuthUserItem::class.java)!!
								//Log.wtf(TAG, "document exists")
							}
							else emitter.onNext(false)
						}
						.addOnFailureListener { emitter.onError(it) }
				}
			}
			auth.addAuthStateListener(authStateListener)
			emitter.setCancellable { auth.removeAuthStateListener(authStateListener) }
		}).subscribeOn(ExecuteSchedulers.io())
	}

	override fun fetchUserInfo(): Single<UserItem> {
		return Single.create(SingleOnSubscribe<UserItem> { emitter ->

			val refBase = firestore.collection(USERS_BASE_COLLECTION_REFERENCE)
				.document(currentUserId)

			val refGeneral = fillUserGeneralRef(authUserItem.baseUserInfo)
			//get general user item first
			refGeneral.get()
				.addOnSuccessListener { remoteUser ->
					if (remoteUser.exists()) {
						val remoteUserItem = remoteUser.toObject(UserItem::class.java)!!
						//check if registration token exists
						fInstance.instanceId
							.addOnSuccessListener { instanceResult ->
								//add new token
								refBase.update(
									USER_BASE_REGISTRATION_TOKENS_FIELD,
									FieldValue.arrayUnion(instanceResult.token))

								userWrapper.setUser(remoteUserItem)
								emitter.onSuccess(remoteUserItem)
								reInit()
							}
							.addOnFailureListener { instanceIdError -> emitter.onError(instanceIdError) }
					} else emitter.onError(Throwable("User does not exist"))

				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(ExecuteSchedulers.io())
	}

	override fun logOut(){
		if (auth.currentUser != null) {
			auth.signOut()
			fbLogin.logOut()
			userWrapper.clearData()
		}
	}

	override fun signIn(token: String): Single<HashMap<Boolean, BaseUserInfo>> =
		signInWithFacebook(token)
			.flatMap { checkAndRetrieveFullUser(it) }
			.subscribeOn(ExecuteSchedulers.io())

	/**
	 * create new [UserItem] documents in db
	 */
	override fun registerUser(userItem: UserItem): Completable =
		Completable.create { emitter ->
			val authUserItem = AuthUserItem(userItem.baseUserInfo)
			val ref = fillUserGeneralRef(userItem.baseUserInfo)
			ref.set(userItem)
				.addOnSuccessListener {
					firestore.collection(USERS_BASE_COLLECTION_REFERENCE)
						.document(userItem.baseUserInfo.userId)
						.set(authUserItem)
						.addOnSuccessListener {
							userWrapper.setUser(userItem)
							emitter.onComplete()
							reInit()
						}
						.addOnFailureListener { emitter.onError(it) }
				}.addOnFailureListener { emitter.onError(it) }
		}.subscribeOn(ExecuteSchedulers.io())


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
						val baseUser = BaseUserInfo(name = firebaseUser.displayName!!,
						                            mainPhotoUrl = photoUrl,
						                            userId = firebaseUser.uid)
						emitter.onSuccess(baseUser)
					}
					else emitter.onError(Exception(it.exception))
				}
				.addOnFailureListener { emitter.onError(it) }
		}).subscribeOn(ExecuteSchedulers.io())

	private fun checkAndRetrieveFullUser(baseUserInfo: BaseUserInfo): Single<HashMap<Boolean, BaseUserInfo>> =
		Single.create(SingleOnSubscribe<HashMap<Boolean, BaseUserInfo>> { emitter ->
			firestore.collection(USERS_BASE_COLLECTION_REFERENCE)
				.document(baseUserInfo.userId)
				.get()
				.addOnSuccessListener { baseUserDoc ->
					//if base info about user exists in db
					if (baseUserDoc.exists()) {
						val userInBase = baseUserDoc.toObject(AuthUserItem::class.java)!!
						//Log.wtf(TAG, "$userInBase")
						firestore.collection(USERS_COLLECTION_REFERENCE)
							.document(userInBase.baseUserInfo.city)
							.collection(userInBase.baseUserInfo.gender)
							.document(userInBase.baseUserInfo.userId)
							.get()
							.addOnSuccessListener { fullUserDoc ->
								//if full user info exists in db => return continue reg flag false
								if (fullUserDoc.exists()) {
									val retrievedUser = fullUserDoc.toObject(UserItem::class.java)!!
									userWrapper.setUser(retrievedUser)
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
		}).subscribeOn(ExecuteSchedulers.io())

}


