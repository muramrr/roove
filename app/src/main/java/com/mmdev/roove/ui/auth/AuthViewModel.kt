/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 22.12.19 16:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.auth.usecase.IsAuthenticatedListenerUseCase
import com.mmdev.business.auth.usecase.LogOutUseCase
import com.mmdev.business.auth.usecase.SignInUseCase
import com.mmdev.business.auth.usecase.SignUpUseCase
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.user.UserItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val isAuthenticatedListener: IsAuthenticatedListenerUseCase,
                                        private val logOut: LogOutUseCase,
                                        private val signIn: SignInUseCase,
                                        private val signUp: SignUpUseCase) :
		ViewModel() {


	val continueRegistration: MutableLiveData<Boolean> = MutableLiveData()

	//experimental
	val error: MutableLiveData<Throwable> = MutableLiveData()

	val showProgress: MutableLiveData<Boolean> = MutableLiveData()

	private val userItem: MutableLiveData<UserItem> = MutableLiveData()

	private val baseUserInfo: MutableLiveData<BaseUserInfo> = MutableLiveData()

	private val isAuthenticatedStatus: MutableLiveData<Boolean> = MutableLiveData()

	private val disposables = CompositeDisposable()

	companion object {
		private const val TAG = "mylogs"
	}

	fun checkIsAuthenticated() {
		disposables.add(isAuthenticatedExecution()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       isAuthenticatedStatus.value = it
                       },
                       {
	                       error.value = it
                           Log.wtf(TAG, it)
                       }))
	}

	fun signIn(loginToken: String) {
		continueRegistration.value = true
		disposables.add(signInExecution(loginToken)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgress.value = true }
            .doFinally { showProgress.value = false }
            .subscribe({
	                       if (it.preferredGender.isNotEmpty()) {
		                       Log.wtf("mylogs", "successfully retrieved user")
		                       continueRegistration.value = false
		                       userItem.value = it
	                       }
	                       else {
		                       baseUserInfo.value = it.baseUserInfo
		                       Log.wtf("mylogs", "received user: =${baseUserInfo.value}")
	                       }
	                       Log.wtf("mylogs", "continue registration? -${continueRegistration.value}")
                       },
                       {
	                       Log.wtf("mylogs", "$it")
                       }
            ))
	}

	fun signUp(userItem: UserItem) {
		disposables.add(signUpExecution(userItem)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
	                       continueRegistration.value = false
	                       this.userItem.value = userItem
                       },
                       {
                           Log.wtf("mylogs", it)
                       }))
	}

	fun logOut() {
		logOutExecution()
	}

	fun getAuthStatus() = isAuthenticatedStatus

	fun getBaseUserInfo() = baseUserInfo

	fun getUserItem() = userItem







	private fun isAuthenticatedExecution() = isAuthenticatedListener.execute()
	private fun logOutExecution() = logOut.execute()
	private fun signInExecution(token: String) = signIn.execute(token)
	private fun signUpExecution(userItem: UserItem) = signUp.execute(userItem)



	override fun onCleared() {
		disposables.clear()
		super.onCleared()
	}


	enum class AuthStatus {
		REGISTRATION_PENDING,
		REGISTRATION_FINISHED,
		USER_AUTHENTICATED
	}
}