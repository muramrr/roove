package com.mmdev.business.conversations.usecase

import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository

/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

class DeleteConversationUseCase (private val repository: ConversationsRepository)  {

	fun execute(t: ConversationItem) = repository.deleteConversation(t)

}