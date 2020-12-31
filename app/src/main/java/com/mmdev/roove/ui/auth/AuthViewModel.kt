/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 15:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth

import androidx.lifecycle.MutableLiveData
import com.mmdev.business.auth.AuthRepository
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.roove.ui.auth.AuthViewModel.AuthenticationState.*
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError
import javax.inject.Inject


class AuthViewModel @Inject constructor(
	private val repo: AuthRepository
) : BaseViewModel() {
	
	val continueRegistration: MutableLiveData<Boolean> = MutableLiveData()
	val showProgress: MutableLiveData<Boolean> = MutableLiveData()

	private val baseUserInfo: MutableLiveData<BaseUserInfo> = MutableLiveData()
	private val authCallbackHandler: MutableLiveData<Boolean> = MutableLiveData()
	val authenticatedState: MutableLiveData<AuthenticationState> = MutableLiveData()
	val actualCurrentUserItem: MutableLiveData<UserItem> = MutableLiveData()


	fun checkIsAuthenticated() {
		disposables.add(repo.isAuthenticatedListener()
            .observeOn(mainThread())
            .subscribe(
	            {
					if (authCallbackHandler.value != it) {
					   authCallbackHandler.value = it
					   when (it) {
					       true -> authenticatedState.value = AUTHENTICATED
					       false -> authenticatedState.value = UNAUTHENTICATED
					   }
					}
	            },
				{ error.value = MyError(ErrorType.AUTHENTICATING, it) }
            )
		)
	}

	fun logOut() {
		repo.logOut()
		authenticatedState.value = UNAUTHENTICATED
	}

	fun fetchUserItem() {
		disposables.add(repo.fetchUserInfo()
            .observeOn(mainThread())
            .subscribe(
	            { actualCurrentUserItem.value = it },
	            { error.value = MyError(ErrorType.FETCHING, it) }
            )
		)
	}

	fun signIn(loginToken: String) {
		disposables.add(repo.signIn(loginToken)
            .observeOn(mainThread())
            .doOnSubscribe { showProgress.value = true }
            .doFinally { showProgress.value = false }
            .subscribe(
	            {
					if (it.containsKey(false)) {
					   continueRegistration.value = false
					}
					else {
					   continueRegistration.value = true
					   baseUserInfo.value = it.getValue(true)
					}
				},
	            { error.value = MyError(ErrorType.AUTHENTICATING, it) }
            ))
	}

	fun register(userItem: UserItem) {
		disposables.add(repo.registerUser(userItem)
            .observeOn(mainThread())
            .subscribe(
	            {
		            continueRegistration.value = false
		            authenticatedState.value = AUTHENTICATED
	            },
	            { error.value = MyError(ErrorType.AUTHENTICATING, it) }
            )
		)
	}



	fun getBaseUserInfo() = baseUserInfo
	

	enum class AuthenticationState {
		UNAUTHENTICATED,    // Initial state, the user needs to authenticate
		AUTHENTICATED,  // The user has authenticated successfully
	}
}