package com.mmdev.meetapp.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.auth.usecase.*
import com.mmdev.business.user.model.User

class AuthViewModel(private val handleHandleUserExistence: HandleUserExistenceUseCase,
                    private val isAuthenticated: IsAuthenticatedUseCase,
                    private val logOut: LogOutUseCase,
                    private val signInWithFacebook: SignInWithFacebookUseCase,
                    private val signUp: SignUpUseCase) : ViewModel() {


    fun handleUserExistence(uId: String) = handleHandleUserExistence.execute(uId)
    fun isAuthenticated() = isAuthenticated.execute()
    fun logOut() = logOut.execute()
    fun signInWithFacebook(token: String) = signInWithFacebook.execute(token)
    fun signUp(user: User) = signUp.execute(user)



}