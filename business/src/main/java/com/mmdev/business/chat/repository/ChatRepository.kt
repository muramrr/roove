/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 03.02.20 19:10
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

	fun getMessagesList(conversation: ConversationItem): Observable<List<MessageItem>>

	fun sendMessage(messageItem: MessageItem, emptyChat: Boolean? = false): Completable

	fun sendPhoto(photoUri: String): Observable<PhotoAttachmentItem>

}