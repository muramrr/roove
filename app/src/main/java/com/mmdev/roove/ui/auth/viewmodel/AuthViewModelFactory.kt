/*
 * Created by Andrii Kovalchuk on 19.06.19 12:05
 * Copyright (c) 2019. All rights reserved.
 * Last modified 24.10.19 18:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.auth.usecase.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class AuthViewModelFactory
@Inject constructor(private val handleHandleUserExistence: HandleUserExistenceUseCase,
                    private val isAuthenticated: IsAuthenticatedUseCase,
                    private val logOut: LogOutUseCase,
                    private val signInWithFacebook: SignInWithFacebookUseCase,
                    private val signUp: SignUpUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(handleHandleUserExistence,isAuthenticated, logOut,
                                 signInWithFacebook, signUp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}