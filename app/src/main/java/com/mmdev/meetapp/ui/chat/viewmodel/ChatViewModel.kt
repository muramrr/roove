package com.mmdev.meetapp.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.domain.user.model.User
import com.mmdev.domain.user.usecase.LoginUseCase
import com.mmdev.domain.user.usecase.SignUpUseCase


class ChatViewModel(private val signUp: SignUpUseCase,
                    private val login: LoginUseCase) : ViewModel() {

    fun signUp(username: String, password: String) = signUp.execute(User(username, password))

    fun login(username: String, password: String) = login.execute(User(username, password))
}