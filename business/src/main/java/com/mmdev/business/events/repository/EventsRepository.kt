/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.events.repository

import com.mmdev.business.events.model.EventItem
import com.mmdev.business.events.model.EventsResponse
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface EventsRepository {

	fun getEvents(): Single<EventsResponse>

	fun getEventDetails(id: Int): Single<EventItem>

}