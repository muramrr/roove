/*
 * Created by Andrii Kovalchuk on 06.06.19 17:19
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository

class GetMessagesUseCase (private val repository: ChatRepository) {

    fun execute(s: String) = repository.getMessagesList(s)

}