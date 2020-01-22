/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 18:55
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.places

import com.mmdev.business.places.repository.PlacesRepository
import com.mmdev.business.user.UserItem
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */


class PlacesRepositoryImpl @Inject constructor(private val placesApi: PlacesApi,
                                               private val currentUser: UserItem):
		PlacesRepository {

	//current time
	private val unixTime = System.currentTimeMillis() / 1000L

	override fun getPlacesList(category: String) =
		placesApi.getPlacesList(unixTime,
		                        category,
		                        currentUser.baseUserInfo.city)


	override fun getPlaceDetails(id: Int) = placesApi.getPlaceDetails(id)


}