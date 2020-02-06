/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.02.20 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.repository

import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.chat.entity.PhotoAttachmentItem
import com.mmdev.business.conversations.ConversationItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ChatRepository {

	fun getConversationWithPartner(partnerId: String): Single<ConversationItem>

	fun loadMessages(conversation: ConversationItem): Single<List<MessageItem>>

	fun observeNewMessages(conversation: ConversationItem): Observable<MessageItem>

	fun sendMessage(messageItem: MessageItem, emptyChat: Boolean? = false): Completable

	fun uploadMessagePhoto(photoUri: String): Observable<PhotoAttachmentItem>

}