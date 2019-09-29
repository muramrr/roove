package com.mmdev.meetapp.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.domain.chat.usecase.GetMessagesUseCase
import com.mmdev.domain.chat.usecase.SendMessageUseCase
import com.mmdev.domain.chat.usecase.SendPhotoUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ChatViewModelFactory @Inject constructor(private val getMessages: GetMessagesUseCase,
                                               private val sendMessage: SendMessageUseCase,
                                               private val sendPhoto: SendPhotoUseCase) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(getMessages, sendMessage, sendPhoto) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}