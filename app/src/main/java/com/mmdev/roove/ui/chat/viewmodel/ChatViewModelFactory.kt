package com.mmdev.roove.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase
import com.mmdev.business.chat.usecase.SetConversationUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ChatViewModelFactory @Inject constructor(private val getMessagesUC: GetMessagesUseCase,
                                               private val sendMessageUC: SendMessageUseCase,
                                               private val sendPhotoUC: SendPhotoUseCase,
                                               private val setConversationUC: SetConversationUseCase) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(getMessagesUC,
                                 sendMessageUC,
                                 sendPhotoUC,
                                 setConversationUC) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}