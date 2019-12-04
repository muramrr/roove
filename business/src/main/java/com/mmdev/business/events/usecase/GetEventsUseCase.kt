/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.events.usecase

import com.mmdev.business.events.repository.EventsRepository

/**
 * This is the documentation block about the class
 */

class GetEventsUseCase (private val repository: EventsRepository) {

	fun execute() = repository.getEvents()

}