package com.mmdev.domain.messages.repository


import com.mmdev.domain.messages.model.ChatModel
import io.reactivex.Completable
import io.reactivex.Observable


interface MessagesRepository {

    fun sendMessage(message: ChatModel): Completable

    fun getMessages(): Observable<List<ChatModel>>
}