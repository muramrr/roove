/*
 * Created by Andrii Kovalchuk on 27.11.19 19:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 27.11.19 19:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.chat.repository.ChatRepository

/**
 * This is the documentation block about the class
 */

class CreateConversationUseCase (private val repository: ChatRepository) {

	fun execute(t: CardItem) = repository.createConversation(t)

}