/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 17:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.places

import com.mmdev.business.places.PlaceDetailedItem
import com.mmdev.business.places.PlacesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlacesApi {

	@GET("places/?fields=id,title,short_title,images&text_format=plain")
	fun getPlacesList(@Query("actual_since") timestamp: Long,
	                  @Query("categories") category: String,
	                  @Query("location") location: String,
	                  @Query("page") page: Int = 1): Single<PlacesResponse>

	@GET("places/{id}/?fields=id,title,short_title,body_text,description,images&text_format=plain")
	fun getPlaceDetails(@Path("id") id: Int): Single<PlaceDetailedItem>

//	@GET("events/?fields=id,title,short_title,images&text_format=plain")
//	fun getEventsList(@Query("actual_since") timestamp: Long,
//	                  @Query("categories") category: String,
//	                  @Query("location") location: String): Single<EventsResponse>
//
//	@GET("events/{id}/?fields=id,title,short_title,body_text,description,images&text_format=plain")
//	fun getEventDetails(@Path("id") id: Int): Single<EventDetailedItem>

}