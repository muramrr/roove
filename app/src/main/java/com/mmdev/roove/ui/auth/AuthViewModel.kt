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

package com.mmdev.roove.ui.auth

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.mmdev.business.auth.AuthRepository
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.roove.core.log.logDebug
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType
import com.mmdev.roove.ui.common.errors.MyError


class AuthViewModel @ViewModelInject constructor(
	private val repo: AuthRepository
) : BaseViewModel() {
	
	val continueRegistration: MutableLiveData<Boolean> = MutableLiveData()
	
	val baseUserInfo = MutableLiveData<BaseUserInfo>()
	

	fun signIn(loginToken: String) {
		disposables.add(repo.signIn(loginToken)
            .observeOn(mainThread())
            .subscribe(
	            { logDebug(TAG, "Logged in successfully") },
	            { error.value = MyError(ErrorType.AUTHENTICATING, it) }
            ))
	}

	fun signUp(userItem: UserItem) {
		disposables.add(repo.signUp(userItem)
            .observeOn(mainThread())
            .subscribe(
	            { continueRegistration.value = true },
	            { error.value = MyError(ErrorType.AUTHENTICATING, it) }
            )
		)
	}
	
}