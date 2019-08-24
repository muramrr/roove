package com.mmdev.domain.messages.repository


import com.mmdev.domain.messages.model.Message
import io.reactivex.Completable
import io.reactivex.Observable


interface MessagesRepository {

    fun sendMessage(message: Message): Completable

    fun getMessages(): Observable<List<Message>>
}