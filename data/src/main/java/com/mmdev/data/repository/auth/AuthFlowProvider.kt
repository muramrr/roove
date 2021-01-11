/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mmdev.data.core.firebase.FIRESTORE_NO_DOCUMENT_EXCEPTION
import com.mmdev.data.core.firebase.toUserItem
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.core.log.logError
import com.mmdev.data.datasource.UserDataSource
import com.mmdev.data.datasource.auth.AuthCollector
import com.mmdev.data.datasource.auth.FirebaseUserState
import com.mmdev.data.datasource.auth.FirebaseUserState.NotNullUser
import com.mmdev.data.datasource.location.LocationDataSource
import com.mmdev.domain.auth.IAuthFlowProvider
import com.mmdev.domain.user.UserState
import com.mmdev.domain.user.data.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.BiFunction
import java.util.concurrent.TimeUnit.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 */

@Singleton
class AuthFlowProvider @Inject constructor(
	private val auth: FirebaseAuth,
	private val fbLogin: LoginManager,
	private val location: LocationDataSource,
	private val userDataSource: UserDataSource
): IAuthFlowProvider {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	companion object {
		private const val LOCATION_FIELD = "location"
	}
	
	private val authObservable: Observable<FirebaseUserState> = AuthCollector(auth).firebaseAuthObservable.map {
		it.currentUser?.reload()
		FirebaseUserState.pack(it.currentUser)
	}
	
	override fun getUserAuthState(): Observable<UserState> = authObservable.switchMap { firebaseUser ->
		logDebug(TAG, "Collecting auth information...")
		
		if (firebaseUser is NotNullUser) {
			
			logDebug(TAG, "Auth info exists: ${firebaseUser.user.uid}")
			
			getUserFromRemoteStorage(firebaseUser.user)
		}
		//not signed in
		else {
			logError(TAG, "Auth info does not exists...")
			
			Observable.just(UserState.UNDEFINED)
		}
	}
	
	
	private fun getUserFromRemoteStorage(firebaseUser: FirebaseUser) =
		userDataSource.getFirestoreUser(firebaseUser.uid)
			.zipWith(
				//timeout if no location emitted
				location.locationSingle(),
				BiFunction { user, location ->
					return@BiFunction user.copy(location = location)
				}
			)
			.toObservable()
			.map {
				userDataSource.updateFirestoreUserField(
					it.baseUserInfo.userId,
					LOCATION_FIELD,
					it.location
				).subscribe {
					logDebug(TAG, "Location was updated")
				}
				UserState.registered(it)
			}
			.onErrorResumeNext { throwable ->
				logError(TAG, "$throwable")
				when (throwable) {
					is NoSuchElementException -> {
						//if no document stored on backend
						if (throwable.message == FIRESTORE_NO_DOCUMENT_EXCEPTION)
							Observable.just(UserState.unregistered(firebaseUser.toUserItem()))
						else Observable.just(UserState.UNDEFINED)
					}
					
					else -> Observable.error(throwable)
				}
				
			}
	
	
	override fun updateUserItem(user: UserItem): Completable =
		userDataSource.writeFirestoreUser(user)
	
	
	override fun logOut(){
		if (auth.currentUser != null) {
			auth.signOut()
			fbLogin.logOut()
		}
	}
	
}