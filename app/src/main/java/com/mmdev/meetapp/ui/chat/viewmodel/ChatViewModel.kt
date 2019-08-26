package com.mmdev.meetapp.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.usecase.GetMessagesUseCase
import com.mmdev.domain.messages.usecase.SendMessageUseCase
import com.mmdev.domain.messages.usecase.SendPhotoUseCase
import java.io.File

class ChatViewModel(private val getMessages: GetMessagesUseCase,
                    private val sendMessage: SendMessageUseCase,
                    private val sendPhoto: SendPhotoUseCase) : ViewModel() {

    fun sendMessage(message: Message) = sendMessage.execute(message)

    fun sendPhoto(message: Message, photo: File) = sendPhoto.execute(message, photo)

    fun getMessages() = getMessages.execute()

}