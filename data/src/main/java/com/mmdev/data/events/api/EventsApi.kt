/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.events.api

import com.mmdev.business.events.model.EventItem
import com.mmdev.business.events.model.EventsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * This is the documentation block about the class
 */

interface EventsApi {

	@GET("events/?location=kev&fields=id,description,short_title,title,images")
	fun getEventsList(@Query("actual_since") timestamp: Long): Single<EventsResponse>

	@GET("events/{id}")
	fun getEventDetails(@Path("id") id: Int): Single<EventItem>

}
