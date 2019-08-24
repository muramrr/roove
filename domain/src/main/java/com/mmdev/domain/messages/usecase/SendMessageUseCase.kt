package com.mmdev.domain.messages.usecase


import com.mmdev.domain.core.CompletableWithParamUseCase
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.repository.MessagesRepository

class SendMessageUseCase(private val repository: MessagesRepository) :
    CompletableWithParamUseCase<Message> {

    override fun execute(t: Message) = repository.sendMessage(t)
}