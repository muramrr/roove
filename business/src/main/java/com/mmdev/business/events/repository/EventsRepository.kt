/*
 * Created by Andrii Kovalchuk on 20.11.19 21:38
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.11.19 21:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.events.repository

import com.mmdev.business.events.model.EventsResponse
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface EventsRepository {

	fun getEvents(): Single<EventsResponse>

}