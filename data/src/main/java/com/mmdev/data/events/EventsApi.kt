/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 16:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.events

import com.mmdev.business.events.entity.EventItem
import com.mmdev.business.events.entity.EventsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * This is the documentation block about the class
 */

interface EventsApi {

	@GET("events/?location=msk&fields=id,description,short_title,title,images")
	fun getEventsList(@Query("actual_since") timestamp: Long): Single<EventsResponse>

	@GET("events/{id}")
	fun getEventDetails(@Path("id") id: Int): Single<EventItem>

}
