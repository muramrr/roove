/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.business.chat

import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.photo.PhotoItem
import com.mmdev.business.user.UserItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ChatRepository {
	
	fun loadMessages(
		conversation: ConversationItem,
		cursorPosition: Int
	): Single<List<MessageItem>>

	fun observeNewMessages(
		user: UserItem,
		conversation: ConversationItem
	): Observable<MessageItem>

	//fun observePartnerOnline(conversationId: String): Observable<Boolean>

	fun sendMessage(messageItem: MessageItem, emptyChat: Boolean? = false): Completable
	
	fun uploadMessagePhoto(photoUri: String, conversationId: String): Single<PhotoItem>

}