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

package com.mmdev.roove.ui.auth

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.auth.AuthRepository
import com.mmdev.business.auth.usecase.*
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.roove.ui.auth.AuthViewModel.AuthenticationState.AUTHENTICATED
import com.mmdev.roove.ui.auth.AuthViewModel.AuthenticationState.UNAUTHENTICATED
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject


class AuthViewModel @Inject constructor(repo: AuthRepository) : BaseViewModel() {

	private val isAuthenticatedListener = IsAuthenticatedListenerUseCase(repo)
	private val fetchUserUC = FetchUserInfoUseCase(repo)
	private val logOut = LogOutUseCase(repo)
	private val signIn = SignInUseCase(repo)
	private val registerUser = RegisterUserUseCase(repo)


	val continueRegistration: MutableLiveData<Boolean> = MutableLiveData()
	val showProgress: MutableLiveData<Boolean> = MutableLiveData()

	private val baseUserInfo: MutableLiveData<BaseUserInfo> = MutableLiveData()
	private val authCallbackHandler: MutableLiveData<Boolean> = MutableLiveData()
	val authenticatedState: MutableLiveData<AuthenticationState> = MutableLiveData()
	val actualCurrentUserItem: MutableLiveData<UserItem> = MutableLiveData()


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

	fun logOut() {
		logOutExecution()
		authenticatedState.value = UNAUTHENTICATED
	}

	fun fetchUserItem() {
		disposables.add(fetchUserInfoExecution()
            .observeOn(mainThread())
            .subscribe({
                           actualCurrentUserItem.value = it
                       },
                       {
                           error.value = MyError(ErrorType.FETCHING, it)
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
	                       error.value = MyError(ErrorType.AUTHENTICATING, it)
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
	                       error.value = MyError(ErrorType.AUTHENTICATING, it)
                       }))
	}



	fun getBaseUserInfo() = baseUserInfo



	private fun isAuthenticatedExecution() = isAuthenticatedListener.execute()
	private fun fetchUserInfoExecution() = fetchUserUC.execute()
	private fun logOutExecution() = logOut.execute()
	private fun signInExecution(token: String) = signIn.execute(token)
	private fun registrationExecution(userItem: UserItem) = registerUser.execute(userItem)

	enum class AuthenticationState {
		UNAUTHENTICATED,    // Initial state, the user needs to authenticate
		AUTHENTICATED,  // The user has authenticated successfully
	}
}