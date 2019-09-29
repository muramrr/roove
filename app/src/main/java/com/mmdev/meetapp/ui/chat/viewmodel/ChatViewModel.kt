package com.mmdev.meetapp.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.domain.chat.model.Message
import com.mmdev.domain.chat.usecase.GetMessagesUseCase
import com.mmdev.domain.chat.usecase.SendMessageUseCase
import com.mmdev.domain.chat.usecase.SendPhotoUseCase

class ChatViewModel(private val getMessages: GetMessagesUseCase,
                    private val sendMessage: SendMessageUseCase,
                    private val sendPhoto: SendPhotoUseCase) : ViewModel() {

    fun getMessages() = getMessages.execute()

    fun sendMessage(message: Message) = sendMessage.execute(message)

    fun sendPhoto(photoUri: String) = sendPhoto.execute(photoUri)



}