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

package com.mmdev.data.datasource.auth

import com.google.firebase.auth.FirebaseAuth
import com.mmdev.data.core.log.logDebug
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate

/**
 * Class used only to provide [FirebaseAuth] callbacks as [Observable]
 */

class AuthCollector(auth: FirebaseAuth) {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	
	internal val firebaseAuthObservable: Observable<FirebaseAuth> = ObservableCreate { emitter ->
		
		val listener = FirebaseAuth.AuthStateListener { emitter.onNext(it) }
		
		auth.addAuthStateListener(listener)
		logDebug(TAG, "AuthListener attached.")
		
		emitter.setCancellable {
			logDebug(TAG, "AuthListener removed.")
			auth.removeAuthStateListener(listener)
		}
	}
	
}