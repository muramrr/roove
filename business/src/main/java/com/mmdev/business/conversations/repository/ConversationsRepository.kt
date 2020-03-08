/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.03.20 18:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations.repository

import com.mmdev.business.conversations.ConversationItem
import io.reactivex.Completable
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface ConversationsRepository {

	fun deleteConversation(conversationItem: ConversationItem): Completable

	fun getConversationsList(): Single<List<ConversationItem>>

	fun getMoreConversationsList(): Single<List<ConversationItem>>

}