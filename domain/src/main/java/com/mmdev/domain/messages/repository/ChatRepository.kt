package com.mmdev.domain.messages.repository


import com.mmdev.domain.messages.model.Message
import com.mmdev.domain.messages.model.PhotoAttached
import io.reactivex.Completable
import io.reactivex.Observable


interface ChatRepository {

    fun getMessages(): Observable<List<Message>>

    fun sendMessage(message: Message): Completable

    fun sendPhoto(photoUri: String): Observable<PhotoAttached>

}