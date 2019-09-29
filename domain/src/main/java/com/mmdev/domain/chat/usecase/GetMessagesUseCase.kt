package com.mmdev.domain.chat.usecase


import com.mmdev.domain.chat.model.Message
import com.mmdev.domain.chat.repository.ChatRepository
import com.mmdev.domain.core.ObservableUseCase

class GetMessagesUseCase (private val repository: ChatRepository) :
    ObservableUseCase<List<Message>> {

    override fun execute() = repository.getMessages()
}