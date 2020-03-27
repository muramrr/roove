/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.auth.usecase.IsAuthenticatedListenerUseCase
import com.mmdev.business.auth.usecase.LogOutUseCase
import com.mmdev.business.auth.usecase.RegisterUserUseCase
import com.mmdev.business.auth.usecase.SignInUseCase
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
import com.mmdev.roove.ui.auth.AuthViewModel.AuthenticationState.AUTHENTICATED
import com.mmdev.roove.ui.auth.AuthViewModel.AuthenticationState.UNAUTHENTICATED
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject


class AuthViewModel @Inject constructor(repo: AuthRepository) : BaseViewModel() {
	private val isAuthenticatedListener = IsAuthenticatedListenerUseCase(repo)
	private val logOut = LogOutUseCase(repo)
	private val signIn = SignInUseCase(repo)
	private val registerUser = RegisterUserUseCase(repo)


	val continueRegistration: MutableLiveData<Boolean> = MutableLiveData()
	val showProgress: MutableLiveData<Boolean> = MutableLiveData()

	private val baseUserInfo: MutableLiveData<BaseUserInfo> = MutableLiveData()
	private val authCallbackHandler: MutableLiveData<Boolean> = MutableLiveData()
	val authenticatedState: MutableLiveData<AuthenticationState> = MutableLiveData()


	fun checkIsAuthenticated() {
		disposables.add(isAuthenticatedExecution()
            .observeOn(mainThread())
            .subscribe({
	                       if (authCallbackHandler.value != it) {
		                       authCallbackHandler.value = it
		                       when (it) {
			                       true -> authenticatedState.value = AUTHENTICATED
			                       false -> authenticatedState.value = UNAUTHENTICATED
		                       }
	                       }
                       },
                       {
	                       error.value = MyError(ErrorType.AUTHENTICATING, it)
                       }))
	}

	fun signIn(loginToken: String) {
		disposables.add(signInExecution(loginToken)
            .observeOn(mainThread())
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

	fun register(userItem: UserItem) {
		disposables.add(registrationExecution(userItem)
            .observeOn(mainThread())
            .subscribe({
	                       continueRegistration.value = false
	                       authenticatedState.value = AUTHENTICATED
                       },
                       {
	                       error.value = MyError(ErrorType.SAVING, it)
                       }))
	}


	fun logOut() = logOutExecution()
	fun getBaseUserInfo() = baseUserInfo



	private fun isAuthenticatedExecution() = isAuthenticatedListener.execute()
	private fun logOutExecution() = logOut.execute()
	private fun signInExecution(token: String) = signIn.execute(token)
	private fun registrationExecution(userItem: UserItem) = registerUser.execute(userItem)

	enum class AuthenticationState {
		UNAUTHENTICATED,    // Initial state, the user needs to authenticate
		AUTHENTICATED,  // The user has authenticated successfully
	}
}