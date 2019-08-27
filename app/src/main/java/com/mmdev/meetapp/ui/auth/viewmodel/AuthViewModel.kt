package com.mmdev.meetapp.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.domain.user.model.User
import com.mmdev.domain.user.usecase.SignUpUseCase
import com.mmdev.domain.user.usecase.UserExistenceUseCase

class AuthViewModel(private val handleUserExistence: UserExistenceUseCase,
                    private val signUp: SignUpUseCase) : ViewModel() {

    fun handleUserExistence(uId: String) = handleUserExistence.execute(uId)
    fun signUp(user: User) = signUp.execute(user)



}