/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 17:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.events

import com.mmdev.business.events.entity.EventItem
import com.mmdev.business.events.entity.EventsResponse
import com.mmdev.business.events.repository.EventsRepository
import io.reactivex.Single
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */


class EventsRepositoryImpl @Inject constructor(private val eventsApi: EventsApi): EventsRepository {

	//current time
	private val unixTime = System.currentTimeMillis() / 1000L


	override fun getEvents(): Single<EventsResponse> {
		//Log.wtf("mylogs", "time = $unixTime")
		return eventsApi.getEventsList(unixTime)
	}

	override fun getEventDetails(id: Int): Single<EventItem> {
		return eventsApi.getEventDetails(id)
	}


}
