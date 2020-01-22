/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 19:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.places.entity

import com.mmdev.business.ImageItem

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
