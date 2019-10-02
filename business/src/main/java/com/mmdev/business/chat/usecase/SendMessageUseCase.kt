package com.mmdev.business.chat.usecase


import com.mmdev.business.chat.model.Message
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.core.usecase.CompletableWithParamUseCase

class SendMessageUseCase(private val repository: ChatRepository) :
		CompletableWithParamUseCase<Message> {

    override fun execute(t: Message) = repository.sendMessage(t)
}