/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 15:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.places

import com.mmdev.business.core.ImageItem

/**
 * This is the documentation block about the class
 */

data class PlaceItem (val id: Int = 0,
                      val title: String = "",
                      val short_title: String = "",
                      val images: List<ImageItem> = listOf())

data class PlacesResponse (val results: List<PlaceItem> = listOf())

data class PlaceDetailedItem (val id: Int = 0,
                              val title: String = "",
                              val short_title: String = "",
                              val body_text: String = "",
                              val description: String = "",
                              val images: List<ImageItem> = listOf())
