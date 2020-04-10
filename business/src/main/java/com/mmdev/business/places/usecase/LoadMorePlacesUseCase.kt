/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 17:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.places.usecase

import com.mmdev.business.places.repository.PlacesRepository

/**
 * This is the documentation block about the class
 */

class LoadMorePlacesUseCase (private val repository: PlacesRepository) {

	fun execute(s: String) = repository.loadMorePlaces(s)

}