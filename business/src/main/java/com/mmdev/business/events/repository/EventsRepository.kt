/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.events.repository

import com.mmdev.business.events.entity.EventItem
import com.mmdev.business.events.entity.EventsResponse
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface EventsRepository {

	fun getEvents(): Single<EventsResponse>

	fun getEventDetails(id: Int): Single<EventItem>

}