package com.mmdev.business.chat.repository


import com.mmdev.business.chat.model.Message
import com.mmdev.business.chat.model.PhotoAttached
import io.reactivex.Completable
import io.reactivex.Observable


interface ChatRepository {

	fun getMessages(): Observable<List<Message>>

	fun sendMessage(message: Message): Completable

	fun sendPhoto(photoUri: String): Observable<PhotoAttached>

}