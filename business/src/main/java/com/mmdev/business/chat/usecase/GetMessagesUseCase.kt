package com.mmdev.business.chat.usecase


import com.mmdev.business.chat.repository.ChatRepository

class GetMessagesUseCase (private val repository: ChatRepository) {

    fun execute(t: String) = repository.getMessagesList(t)

}