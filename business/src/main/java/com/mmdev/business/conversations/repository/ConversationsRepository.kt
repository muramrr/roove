/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 19:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations.repository

import com.mmdev.business.conversations.model.ConversationItem
import io.reactivex.Completable
import io.reactivex.Observable

/**
 * This is the documentation block about the class
 */

interface ConversationsRepository {

	fun getConversationsList(): Observable<List<ConversationItem>>

	fun deleteConversation(conversationItem: ConversationItem): Completable

}