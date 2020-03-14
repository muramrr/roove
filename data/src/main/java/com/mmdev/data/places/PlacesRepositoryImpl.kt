/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.03.20 16:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.places

import com.mmdev.business.places.repository.PlacesRepository
import com.mmdev.data.user.UserWrapper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class PlacesRepositoryImpl @Inject constructor(private val placesApi: PlacesApi,
                                               userWrapper: UserWrapper):
		PlacesRepository {

	private val currentUser = userWrapper.getUser()

	//current time
	private val unixTime = System.currentTimeMillis() / 1000L

	override fun getPlacesList(category: String) =
		placesApi.getPlacesList(unixTime,
		                        category,
		                        currentUser.baseUserInfo.city)


	override fun getPlaceDetails(id: Int) = placesApi.getPlaceDetails(id)


}