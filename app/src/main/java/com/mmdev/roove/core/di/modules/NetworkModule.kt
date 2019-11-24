/*
 * Created by Andrii Kovalchuk on 24.11.19 17:49
 * Copyright (c) 2019. All rights reserved.
 * Last modified 23.11.19 19:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import com.mmdev.data.events.api.EventsApi
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Module which provides all required dependencies about network
 */
@Module
class NetworkModule {

	/**
	 * Provides the Post service implementation.
	 * @param retrofit the Retrofit object used to instantiate the service
	 * @return the Post service implementation.
	 */
	@Provides
	@Singleton
	fun eventsApi(retrofit: Retrofit): EventsApi = retrofit.create(EventsApi::class.java)

	/**
	 * Provides the Retrofit object.
	 * @return the Retrofit object
	 */
	@Provides
	@Singleton
	fun retrofitInterface(): Retrofit = Retrofit.Builder()
			.baseUrl("https://kudago.com/public-api/v1.4/")
			.addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
			.addConverterFactory(GsonConverterFactory.create())
			.build()
}