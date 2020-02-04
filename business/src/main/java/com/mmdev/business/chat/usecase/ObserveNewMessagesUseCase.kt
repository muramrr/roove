/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.02.20 18:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.ConversationItem

class ObserveNewMessagesUseCase (private val repository: ChatRepository) {

    fun execute(c: ConversationItem) = repository.observeNewMessages(c)

}