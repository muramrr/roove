/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.places.repository

import com.mmdev.business.places.PlaceDetailedItem
import com.mmdev.business.places.PlacesResponse
import io.reactivex.rxjava3.core.Single


/**
 * This is the documentation block about the class
 */

interface PlacesRepository {

	fun getPlacesList(category: String): Single<PlacesResponse>

	fun getPlaceDetails(id: Int): Single<PlaceDetailedItem>

}