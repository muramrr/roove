package com.mmdev.business.chat.repository


import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.model.PhotoAttachementItem
import io.reactivex.Completable
import io.reactivex.Observable


interface ChatRepository {

	fun getMessagesList(conversationId: String): Observable<List<MessageItem>>

	fun sendMessage(messageItem: MessageItem): Completable

	fun sendPhoto(photoUri: String): Observable<PhotoAttachementItem>

	fun setConversation(conversationId: String)

}