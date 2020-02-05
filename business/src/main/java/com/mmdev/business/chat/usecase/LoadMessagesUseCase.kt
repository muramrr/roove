/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 05.02.20 15:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository
import com.mmdev.business.conversations.ConversationItem

/**
 * This is the documentation block about the class
 */

class LoadMessagesUseCase (private val repository: ChatRepository) {

	fun execute(conversation: ConversationItem) = repository.loadMessages(conversation)

}