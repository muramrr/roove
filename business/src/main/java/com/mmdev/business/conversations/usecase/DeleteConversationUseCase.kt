/*
 * Created by Andrii Kovalchuk on 26.10.19 17:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 30.10.19 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations.usecase

import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.conversations.repository.ConversationsRepository

/**
 * This is the documentation block about the class
 */

class DeleteConversationUseCase (private val repository: ConversationsRepository)  {

	fun execute(t: ConversationItem) = repository.deleteConversation(t)

}