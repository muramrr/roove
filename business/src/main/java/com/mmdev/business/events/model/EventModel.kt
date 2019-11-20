/*
 * Created by Andrii Kovalchuk on 20.11.19 21:38
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.11.19 21:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.events.model

/**
 * This is the documentation block about the class
 */

data class EventItem (val id: Int,
                      val title: String,
                      val slug: String)

data class EventsResponse (val results: List<EventItem>)
