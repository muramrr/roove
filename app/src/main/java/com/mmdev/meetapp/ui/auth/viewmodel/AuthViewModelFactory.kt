package com.mmdev.meetapp.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.domain.user.usecase.SignUpUseCase
import com.mmdev.domain.user.usecase.UserExistenceUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class AuthViewModelFactory @Inject constructor(private val handleUserExistence: UserExistenceUseCase,
                                               private val signUp: SignUpUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(handleUserExistence, signUp) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}