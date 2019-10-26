package com.mmdev.business.chat.usecase


import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.core.usecase.CompletableWithParamUseCase

class SendMessageUseCase(private val repository: ChatRepository) :
		CompletableWithParamUseCase<MessageItem> {

    override fun execute(t: MessageItem) = repository.sendMessage(t)
}