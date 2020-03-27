/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import com.mmdev.data.places.PlacesApi
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about network requests (not firebase)
 */

@Module
class RetrofitModule {


	companion object{
		private const val KUDAGO_BASE_URL = "https://kudago.com/public-api/v1.4/"
	}
	/**
	 * Provides the Post service implementation.
	 * @param retrofit the Retrofit object used to instantiate the service
	 * @return the Post service implementation.
	 */
//	@Provides
//	@Singleton
//	fun eventsApi(retrofit: Retrofit): EventsApi = retrofit.create(EventsApi::class.java)

	@Provides
	@Singleton
	fun placesApi(retrofit: Retrofit): PlacesApi = retrofit.create(PlacesApi::class.java)

	/**
	 * Provides the Retrofit object.
	 * @return the Retrofit object
	 */
	@Provides
	@Singleton
	fun retrofitInterface(): Retrofit = Retrofit.Builder()
		.baseUrl(KUDAGO_BASE_URL)
		.addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
		.addConverterFactory(GsonConverterFactory.create())
		.build()
}