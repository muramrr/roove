package com.mmdev.domain.messages.usecase


import com.mmdev.domain.core.ObservableUseCase
import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.repository.ChatRepository

class GetMessagesUseCase(private val repository: ChatRepository) :
    ObservableUseCase<List<Message>> {

    override fun execute() = repository.getMessages()
}