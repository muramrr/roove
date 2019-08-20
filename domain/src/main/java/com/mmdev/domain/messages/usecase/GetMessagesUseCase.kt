package com.mmdev.domain.messages.usecase


import com.mmdev.domain.core.ObservableUseCase
import com.mmdev.domain.messages.model.ChatModel
import com.mmdev.domain.messages.repository.MessagesRepository

class GetMessagesUseCase(private val repository: MessagesRepository) :
    ObservableUseCase<List<ChatModel>> {

    override fun execute() = repository.getMessages()
}