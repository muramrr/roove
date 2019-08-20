package com.mmdev.domain.messages.usecase


import com.mmdev.domain.core.CompletableWithParamUseCase
import com.mmdev.domain.messages.model.ChatModel
import com.mmdev.domain.messages.repository.MessagesRepository

class SendMessageUseCase(private val repository: MessagesRepository) :
    CompletableWithParamUseCase<ChatModel> {

    override fun execute(t: ChatModel) = repository.sendMessage(t)
}