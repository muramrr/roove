/*
 * Created by Andrii Kovalchuk on 30.11.19 21:17
 * Copyright (c) 2019. All rights reserved.
 * Last modified 30.11.19 21:11
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.model.ConversationItem

class GetMessagesUseCase (private val repository: ChatRepository) {

    fun execute(c: ConversationItem) = repository.getMessagesList(c)

}