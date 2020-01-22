/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 18:35
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

class GetPlaceDetailsUseCase (private val repository: PlacesRepository) {

	fun execute(id: Int) = repository.getPlaceDetails(id)

}