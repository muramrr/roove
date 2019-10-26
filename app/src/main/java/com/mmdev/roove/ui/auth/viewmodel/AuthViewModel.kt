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