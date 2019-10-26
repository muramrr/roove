package com.mmdev.roove.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ChatViewModelFactory @Inject constructor(private val getMessagesUseCase: GetMessagesUseCase,
                                               private val sendMessageUseCase: SendMessageUseCase,
                                               private val sendPhotoUseCase: SendPhotoUseCase) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(getMessagesUseCase, sendMessageUseCase, sendPhotoUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}