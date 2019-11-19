/*
 * Created by Andrii Kovalchuk on 06.06.19 17:23
 * Copyright (c) 2019. All rights reserved.
 * Last modified 28.10.19 18:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.chat.repository.ChatRepository

class SendMessageUseCase(private val repository: ChatRepository) {

    fun execute(t: MessageItem) = repository.sendMessage(t)

}