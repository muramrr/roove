package com.mmdev.roove.ui.chat.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.usecase.GetMessagesUseCase
import com.mmdev.business.chat.usecase.SendMessageUseCase
import com.mmdev.business.chat.usecase.SendPhotoUseCase

class ChatViewModel(private val getMessagesUseCase: GetMessagesUseCase,
                    private val sendMessageUseCase: SendMessageUseCase,
                    private val sendPhotoUseCase: SendPhotoUseCase) : ViewModel() {

    fun getMessages(conversationId: String) = getMessagesUseCase.execute(conversationId)

    fun sendMessage(messageItem: MessageItem) = sendMessageUseCase.execute(messageItem)

    fun sendPhoto(photoUri: String) = sendPhotoUseCase.execute(photoUri)



}