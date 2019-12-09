/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.events

import com.mmdev.business.events.entity.EventItem
import com.mmdev.business.events.entity.EventsResponse
import com.mmdev.business.events.repository.EventsRepository
import com.mmdev.data.events.api.EventsApi
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class EventsRepositoryImpl @Inject constructor(private val eventsApi: EventsApi): EventsRepository{

	override fun getEvents(): Single<EventsResponse> {
		val unixTime = System.currentTimeMillis() / 1000L
		//Log.wtf("mylogs", "time = $unixTime")
		return eventsApi.getEventsList(unixTime)


	}

	override fun getEventDetails(id: Int): Single<EventItem> {
		return eventsApi.getEventDetails(id)
	}

}
