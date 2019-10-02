package com.mmdev.business.chat.usecase


import com.mmdev.business.chat.model.Message
import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.core.usecase.ObservableUseCase

class GetMessagesUseCase (private val repository: ChatRepository) :
		ObservableUseCase<List<Message>> {

    override fun execute() = repository.getMessages()
}