/*
 * Created by Andrii Kovalchuk on 22.11.19 19:36
 * Copyright (c) 2019. All rights reserved.
 * Last modified 22.11.19 19:20
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core

import android.app.Application
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics


class App : Application() {

	override fun onCreate() {
		super.onCreate()
		AppEventsLogger.activateApp(this)
		injector = DaggerAppComponent.builder()
			.application(this)
			.build()
		FirebaseAnalytics.getInstance(this)
	}
}

lateinit var injector: AppComponent