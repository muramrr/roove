/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.03.20 16:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.auth.usecase.IsAuthenticatedListenerUseCase
import com.mmdev.business.auth.usecase.LogOutUseCase
import com.mmdev.business.auth.usecase.SignInUseCase
import com.mmdev.business.auth.usecase.SignUpUseCase
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val isAuthenticatedListener: IsAuthenticatedListenerUseCase,
                                        private val logOut: LogOutUseCase,
                                        private val signIn: SignInUseCase,
                                        private val signUp: SignUpUseCase) :
		BaseViewModel() {


	val continueRegistration: MutableLiveData<Boolean> = MutableLiveData()

	val showProgress: MutableLiveData<Boolean> = MutableLiveData()

	private val baseUserInfo: MutableLiveData<BaseUserInfo> = MutableLiveData()

	private val isAuthenticatedStatus: MutableLiveData<Boolean> = MutableLiveData()


	fun checkIsAuthenticated() {
		disposables.add(isAuthenticatedExecution()
            .debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       if (isAuthenticatedStatus.value != it) isAuthenticatedStatus.value = it
                       },
                       {
	                       error.value = MyError(ErrorType.AUTHENTICATING, it)
                       }))
	}

	fun signIn(loginToken: String) {
		disposables.add(signInExecution(loginToken)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress.value = true }
            .doFinally { showProgress.value = false }
            .subscribe({
	                       if (it.containsKey(false)) {
		                       continueRegistration.value = false
	                       }
	                       else {
		                       continueRegistration.value = true
		                       baseUserInfo.value = it.getValue(true)
	                       }
                       },
                       {
	                       error.value = MyError(ErrorType.SENDING, it)
                       }
            ))
	}

	fun signUp(userItem: UserItem) {
		disposables.add(signUpExecution(userItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       continueRegistration.value = false
	                       isAuthenticatedStatus.value = true
                       },
                       {
	                       error.value = MyError(ErrorType.SAVING, it)
                       }))
	}


	fun logOut() = logOutExecution()
	fun getAuthStatus() = isAuthenticatedStatus
	fun getBaseUserInfo() = baseUserInfo



	private fun isAuthenticatedExecution() = isAuthenticatedListener.execute()
	private fun logOutExecution() = logOut.execute()
	private fun signInExecution(token: String) = signIn.execute(token)
	private fun signUpExecution(userItem: UserItem) = signUp.execute(userItem)
}