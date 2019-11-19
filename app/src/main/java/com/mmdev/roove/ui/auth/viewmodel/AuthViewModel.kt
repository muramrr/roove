/*
 * Created by Andrii Kovalchuk on 19.06.19 12:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 28.10.19 18:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.auth.usecase.*
import com.mmdev.business.user.model.UserItem

class AuthViewModel(private val handleHandleUserExistence: HandleUserExistenceUseCase,
                    private val isAuthenticated: IsAuthenticatedUseCase,
                    private val logOut: LogOutUseCase,
                    private val signInWithFacebook: SignInWithFacebookUseCase,
                    private val signUp: SignUpUseCase) : ViewModel() {


    fun handleUserExistence(uId: String) = handleHandleUserExistence.execute(uId)
    fun isAuthenticated() = isAuthenticated.execute()
    fun logOut() = logOut.execute()
    fun signInWithFacebook(token: String) = signInWithFacebook.execute(token)
    fun signUp(userItem: UserItem) = signUp.execute(userItem)



}