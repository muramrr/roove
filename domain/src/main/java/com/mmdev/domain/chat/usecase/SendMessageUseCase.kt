package com.mmdev.domain.chat.usecase


import com.mmdev.domain.chat.model.Message
import com.mmdev.domain.chat.repository.ChatRepository
import com.mmdev.domain.core.CompletableWithParamUseCase

class SendMessageUseCase(private val repository: ChatRepository) :
    CompletableWithParamUseCase<Message> {

    override fun execute(t: Message) = repository.sendMessage(t)
}