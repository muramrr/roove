package com.mmdev.business.conversations.repository

import com.mmdev.business.conversations.model.ConversationItem
import io.reactivex.Completable
import io.reactivex.Observable


/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

interface ConversationsRepository {

	fun createConversation(): Completable

	fun getConversationsList(): Observable<List<ConversationItem>>

	fun deleteConversation(conversationId: String): Completable



}