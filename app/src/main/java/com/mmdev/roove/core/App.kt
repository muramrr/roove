/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.01.20 20:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.mmdev.roove.R
import com.mmdev.roove.core.di.AppComponent
import com.mmdev.roove.core.di.DaggerAppComponent


lateinit var injector: AppComponent

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		AppEventsLogger.activateApp(this)
		FirebaseAnalytics.getInstance(this)

		injector = DaggerAppComponent.builder()
			.application(this)
			.build()

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// Create the NotificationChannel
			val descriptionText = getString(R.string.notification_channel_description)
			val mChannel = NotificationChannel(getString(R.string.notification_channel_id),
			                                   getString(R.string.notification_channel_name),
			                                   NotificationManager.IMPORTANCE_DEFAULT)
				.apply { description = descriptionText }
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(mChannel)
		}

	}
}

