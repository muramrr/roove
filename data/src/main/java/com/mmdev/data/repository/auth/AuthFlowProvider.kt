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
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.auth.IAuthFlowProvider
import com.mmdev.business.user.UserItem
import com.mmdev.data.core.firebase.getAndDeserializeAsSingle
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.core.log.logError
import com.mmdev.data.datasource.AuthCollector
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 *
 */

@Singleton
class AuthFlowProvider @Inject constructor(
	private val auth: FirebaseAuth,
	private val fbLogin: LoginManager,
	private val fs: FirebaseFirestore
): IAuthFlowProvider {
	
	companion object {
		private const val USERS_COLLECTION = "users"
		private const val TAG = "mylogs_UserProvider"
	}
	
	private val authObservable = AuthCollector(auth).firebaseAuthObservable.map {
		it.currentUser
	}
	
	override fun getUser(): Observable<UserItem?> = authObservable.switchMap { firebaseUser ->
		logDebug(TAG, "Collecting auth information...")
		
		if (firebaseUser != null) {
			
			logDebug(TAG, "Auth info exists: ${firebaseUser.uid}")
			
			getUserFromRemoteStorage(firebaseUser)
		}
		//not signed in
		else {
			logError(TAG, "Auth info does not exists...")
			
			null
		}
	}
	
	
	private fun getUserFromRemoteStorage(firebaseUser: FirebaseUser): Observable<UserItem?> =
		fs.collection(USERS_COLLECTION)
			.document(firebaseUser.uid)
			.getAndDeserializeAsSingle(UserItem::class.java)
			.toObservable()
	
	
	override fun logOut(){
		if (auth.currentUser != null) {
			auth.signOut()
			fbLogin.logOut()
		}
	}
	
}