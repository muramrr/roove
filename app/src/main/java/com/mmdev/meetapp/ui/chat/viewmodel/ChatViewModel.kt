package com.mmdev.meetapp.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.usecase.GetMessagesUseCase
import com.mmdev.domain.messages.usecase.SendMessageUseCase

class ChatViewModel(private val getMessages: GetMessagesUseCase,
                    private val sendMessage: SendMessageUseCase) : ViewModel() {

    fun sendMessage(message: Message) = sendMessage.execute(message)

    fun getMessages() = getMessages.execute()
}