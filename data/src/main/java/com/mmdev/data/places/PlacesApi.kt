/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 18:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.places

import com.mmdev.business.places.entity.PlaceDetailedItem
import com.mmdev.business.places.entity.PlacesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * This is the documentation block about the class
 */

interface PlacesApi {

	@GET("places/?fields=id,title,short_title,images&text_format=plain")
	fun getPlacesList(@Query("actual_since") timestamp: Long,
	                  @Query("categories") category: String,
	                  @Query("location") location: String): Single<PlacesResponse>

	@GET("places/{id}/?fields=id,title,short_title,body_text,description,images&text_format=plain")
	fun getPlaceDetails(@Path("id") id: Int): Single<PlaceDetailedItem>

}