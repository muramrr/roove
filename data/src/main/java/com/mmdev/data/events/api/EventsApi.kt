/*
 * Created by Andrii Kovalchuk on 20.11.19 21:38
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.11.19 21:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.events.api

import com.mmdev.business.events.model.EventsResponse
import io.reactivex.Single
import retrofit2.http.GET

/**
 * This is the documentation block about the class
 */

interface EventsApi {

	@GET("events/?location=kev&is_free=true&actual_since=1574278592")
	fun getEventsList(): Single<EventsResponse>

}
