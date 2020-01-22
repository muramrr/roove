/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 18:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.places.repository

import com.mmdev.business.places.entity.PlaceDetailedItem
import com.mmdev.business.places.entity.PlacesResponse
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface PlacesRepository {

	fun getPlacesList(category: String): Single<PlacesResponse>

	fun getPlaceDetails(id: Int): Single<PlaceDetailedItem>

}