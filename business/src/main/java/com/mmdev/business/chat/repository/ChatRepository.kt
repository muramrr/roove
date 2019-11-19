/*
 * Created by Andrii Kovalchuk on 06.06.19 14:39
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.repository

import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.model.PhotoAttachementItem
import io.reactivex.Completable
import io.reactivex.Observable

interface ChatRepository {

	fun getMessagesList(conversationId: String): Observable<List<MessageItem>>

	fun sendMessage(messageItem: MessageItem): Completable

	fun sendPhoto(photoUri: String): Observable<PhotoAttachementItem>

	fun setConversation(conversationId: String)

}