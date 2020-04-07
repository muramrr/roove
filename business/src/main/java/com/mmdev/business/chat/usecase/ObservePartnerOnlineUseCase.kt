/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 15:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.usecase

import com.mmdev.business.chat.repository.ChatRepository

/**
 * This is the documentation block about the class
 */

class ObservePartnerOnlineUseCase (private val repository: ChatRepository) {

	fun execute(t: String) = repository.observePartnerOnline(t)

}