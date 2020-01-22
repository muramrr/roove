/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 16:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.events.entity

import com.mmdev.business.ImageItem

/**
 * This is the documentation block about the class
 */

data class EventItem (val id: Int = 0,
                      val description: String = "",
                      val short_title: String = "",
                      val title: String = "",
                      val images: List<ImageItem> = listOf())

data class EventsResponse (val results: List<EventItem> = listOf())