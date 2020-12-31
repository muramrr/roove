/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:31
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di

import android.app.Application
import com.mmdev.roove.core.di.modules.AuthModule
import com.mmdev.roove.core.di.modules.FirebaseModule
import com.mmdev.roove.core.di.modules.RepositoryModule
import com.mmdev.roove.core.di.modules.UserModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
	AuthModule::class,
	FirebaseModule::class,
	RepositoryModule::class,
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

}