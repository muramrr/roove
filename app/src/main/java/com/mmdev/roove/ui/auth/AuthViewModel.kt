/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 21:34
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.auth.usecase.*
import com.mmdev.business.user.model.UserItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val handleHandleUserExistence: HandleUserExistenceUseCase,
                                        private val isAuthenticated: IsAuthenticatedUseCase,
                                        private val logOut: LogOutUseCase,
                                        private val signInWithFacebook: SignInWithFacebookUseCase,
                                        private val signUp: SignUpUseCase) :
		ViewModel() {


	val continueRegistration: MutableLiveData<Boolean> = MutableLiveData()

	//experimental
	val error: MutableLiveData<Throwable> = MutableLiveData()

	val showProgress: MutableLiveData<Boolean> = MutableLiveData()

	private val userItemModel: MutableLiveData<UserItem> = MutableLiveData()

	private val isAuthenticatedStatus: MutableLiveData<Boolean> = MutableLiveData()

	private val signUpStatus: MutableLiveData<Boolean> = MutableLiveData()

	private val disposables = CompositeDisposable()

	companion object {
		private const val TAG = "mylogs"
	}

	fun checkIsAuthenticated() {
		disposables.add(isAuthenticatedExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                           if (it == false) {
	                           isAuthenticatedStatus.value = it
	                           Log.wtf(TAG, "USER IS NOT LOGGED IN")

                           }
                           else {
	                           isAuthenticatedStatus.value = it
	                           Log.wtf(TAG, "USER IS LOGGED IN")
                           }
                       },
                       {
	                       error.value = it
                           Log.wtf(TAG, it)
                       }))
	}

	fun signInWithFacebook(loginToken: String) {
		disposables.add(signInWithFacebookExecution(loginToken)
            .flatMap {
                userItemModel.value = it
                handleUserExistenceExecution(it.userId)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress.value = true}
            .doFinally { showProgress.value = false }
            .subscribe({
	                       Log.wtf("mylogs", "successfully signed in")
	                       isAuthenticatedStatus.value = true
	                       continueRegistration.value = false
                           userItemModel.value = it
                       },
                       {
	                       Log.wtf("mylogs", it)
	                       continueRegistration.value = true
                       }
            ))
	}

	fun signUp(userItem: UserItem) {
		disposables.add(signUpExecution(userItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       isAuthenticatedStatus.value = true
	                       continueRegistration.value = false
                       },
                       {
                           Log.wtf("mylogs", it)
                       }))
	}

	fun getAuthStatus() = isAuthenticatedStatus
	fun getSignUpStatus() = signUpStatus

	private fun handleUserExistenceExecution(uId: String) = handleHandleUserExistence.execute(uId)
	private fun isAuthenticatedExecution() = isAuthenticated.execute()
	fun logOut() = logOut.execute()
	private fun signInWithFacebookExecution(token: String) = signInWithFacebook.execute(token)
	private fun signUpExecution(userItem: UserItem) = signUp.execute(userItem)


	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}
}