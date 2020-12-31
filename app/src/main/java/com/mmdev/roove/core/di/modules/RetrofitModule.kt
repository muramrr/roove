/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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