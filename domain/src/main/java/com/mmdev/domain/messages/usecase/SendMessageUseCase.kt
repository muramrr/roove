package com.mmdev.domain.messages.usecase


import com.mmdev.domain.core.CompletableWithParamUseCase
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.repository.ChatRepository

class SendMessageUseCase(private val repository: ChatRepository) :
    CompletableWithParamUseCase<Message> {

    override fun execute(t: Message) = repository.sendMessage(t)
}