/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 19:50
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.repository

import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.model.PhotoAttachementItem
import com.mmdev.business.conversations.model.ConversationItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface ChatRepository {

	fun createConversation(partnerCardItem: CardItem): Single<ConversationItem>

	fun getMessagesList(conversationId: String): Observable<List<MessageItem>>

	fun sendMessage(messageItem: MessageItem): Completable

	fun sendPhoto(photoUri: String): Observable<PhotoAttachementItem>

}