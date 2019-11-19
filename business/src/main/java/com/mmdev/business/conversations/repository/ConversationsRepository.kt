/*
 * Created by Andrii Kovalchuk on 26.10.19 16:40
 * Copyright (c) 2019. All rights reserved.
 * Last modified 01.11.19 20:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations.repository

import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface ConversationsRepository {

	fun createConversation(partnerCardItem: CardItem): Single<ConversationItem>

	fun getConversationsList(): Observable<List<ConversationItem>>

	fun deleteConversation(conversationItem: ConversationItem): Completable

}