/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 08.03.20 18:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations.usecase

import com.mmdev.business.conversations.repository.ConversationsRepository

/**
 * This is the documentation block about the class
 */

class GetMoreConversationsListUseCase (private val repository: ConversationsRepository) {

	fun execute() = repository.getMoreConversationsList()

}