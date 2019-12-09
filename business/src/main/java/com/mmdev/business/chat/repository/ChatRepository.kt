/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.repository

import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.chat.entity.PhotoAttachementItem
import com.mmdev.business.conversations.entity.ConversationItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ChatRepository {

	fun getConversationWithPartner(partnerId: String): Single<ConversationItem>

	fun getMessagesList(conversation: ConversationItem): Observable<List<MessageItem>>

	fun sendMessage(messageItem: MessageItem, emptyChat: Boolean? = false): Completable

	fun sendPhoto(photoUri: String): Observable<PhotoAttachementItem>

}