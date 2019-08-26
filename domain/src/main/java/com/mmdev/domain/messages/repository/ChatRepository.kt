package com.mmdev.domain.messages.repository


import com.mmdev.domain.messages.model.Message
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.File


interface ChatRepository {

    fun getMessages(): Observable<List<Message>>

    fun sendMessage(message: Message): Completable

    fun sendPhoto(message: Message, photo: File): Completable

}