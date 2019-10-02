package com.mmdev.meetapp.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.chat.model.Message
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase

class ChatViewModel(private val getMessages: GetMessagesUseCase,
                    private val sendMessage: SendMessageUseCase,
                    private val sendPhoto: SendPhotoUseCase) : ViewModel() {

    fun getMessages() = getMessages.execute()

    fun sendMessage(message: Message) = sendMessage.execute(message)

    fun sendPhoto(photoUri: String) = sendPhoto.execute(photoUri)



}