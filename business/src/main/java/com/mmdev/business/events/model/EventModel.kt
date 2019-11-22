/*
 * Created by Andrii Kovalchuk on 21.11.19 21:02
 * Copyright (c) 2019. All rights reserved.
 * Last modified 21.11.19 20:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.events.model

/**
 * This is the documentation block about the class
 */

data class EventItem (val id: Int = 0,
                      val description: String = "",
                      val short_title: String = "",
                      val title: String = "",
                      val images: List<ImageItem> = listOf())

data class EventsResponse (val results: List<EventItem> = listOf())

//image url
data class ImageItem (val image: String = "")