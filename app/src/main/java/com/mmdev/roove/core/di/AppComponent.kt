/*
 * Created by Andrii Kovalchuk on 26.11.19 20:29
 * Copyright (c) 2019. All rights reserved.
 * Last modified 26.11.19 17:48
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di

import android.app.Application
import com.mmdev.roove.core.di.modules.*
import com.mmdev.roove.core.di.viewmodel.ViewModelFactory
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
	AuthModule::class,
	FirebaseModule::class,
	RepositoryModule::class,
	NetworkModule::class,
	ViewModelModule::class,
	UserModule::class
])
@Singleton
interface AppComponent {

	@Component.Builder
	interface Builder {

		@BindsInstance
		fun application(application: Application): Builder

		fun build(): AppComponent
	}

	fun factory(): ViewModelFactory

}