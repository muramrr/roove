package com.mmdev.business.conversations.repository

import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

interface ConversationsRepository {

	fun createConversation(partnerCardItem: CardItem): Single<ConversationItem>

	fun getConversationsList(): Observable<List<ConversationItem>>

	fun deleteConversation(conversationItem: ConversationItem): Completable



}