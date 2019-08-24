package com.mmdev.meetapp.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.domain.user.usecase.LoginUseCase
import com.mmdev.domain.user.usecase.SignUpUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ChatViewModelFactory @Inject constructor(private val signUp: SignUpUseCase,
                                               private val login: LoginUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(signUp, login) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}