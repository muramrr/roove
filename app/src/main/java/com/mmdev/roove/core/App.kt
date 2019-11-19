/*
 * Created by Andrii Kovalchuk on 19.08.19 16:11
 * Copyright (c) 2019. All rights reserved.
 * Last modified 14.11.19 19:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core

import android.app.Application
import com.facebook.appevents.AppEventsLogger


class App : Application() {

	override fun onCreate() {
		super.onCreate()
		AppEventsLogger.activateApp(this)
		injector = DaggerAppComponent.builder()
			.application(this)
			.build()
	}
}

lateinit var injector: AppComponent