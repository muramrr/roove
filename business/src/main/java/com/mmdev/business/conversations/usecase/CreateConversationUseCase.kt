package com.mmdev.business.conversations.usecase

import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.repository.ConversationsRepository

/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

class CreateConversationUseCase (private val repository: ConversationsRepository) {

	fun execute(t: CardItem) = repository.createConversation(t)

}