/*
 * Created by Andrii Kovalchuk on 26.08.19 03:36
 * Copyright (c) 2019. All rights reserved.
 * Last modified 02.10.19 18:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository

class SendPhotoUseCase (private val repository: ChatRepository) {

	fun execute(t: String) = repository.sendPhoto(t)

}

