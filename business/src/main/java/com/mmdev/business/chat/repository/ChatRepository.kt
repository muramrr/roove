/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 15.03.20 17:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.repository

import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.PhotoItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ChatRepository {

	fun loadMessages(conversation: ConversationItem): Single<List<MessageItem>>

	fun loadMoreMessages(): Single<List<MessageItem>>

	fun observeNewMessages(conversation: ConversationItem): Observable<MessageItem>

	fun sendMessage(messageItem: MessageItem, emptyChat: Boolean? = false): Completable

	fun uploadMessagePhoto(photoUri: String): Observable<PhotoItem>

}