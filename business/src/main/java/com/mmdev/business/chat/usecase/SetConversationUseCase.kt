package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository

/* Created by A on 29.10.2019.*/

/**
 * This is the documentation block about the class
 */

class SetConversationUseCase (private val repository: ChatRepository) {

	fun execute(t: String) = repository.setConversation(t)

}