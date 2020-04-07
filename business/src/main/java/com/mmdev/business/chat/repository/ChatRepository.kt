/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 15:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.repository

import com.mmdev.business.chat.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.PhotoItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ChatRepository {

	fun loadMessages(conversation: ConversationItem): Single<List<MessageItem>>

	fun loadMoreMessages(): Single<List<MessageItem>>

	fun observeNewMessages(conversation: ConversationItem): Observable<MessageItem>

	fun observePartnerOnline(conversationId: String): Observable<Boolean>

	fun sendMessage(messageItem: MessageItem, emptyChat: Boolean? = false): Completable

	fun uploadMessagePhoto(photoUri: String, conversationId: String): Observable<PhotoItem>

}