/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.02.20 20:39
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
			val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
			// Create the NotificationChannel
			val messageNotificationChannel =
				NotificationChannel(getString(R.string.notification_channel_id_messages),
				                    getString(R.string.notification_channel_name_messages),
				                    NotificationManager.IMPORTANCE_DEFAULT)
				.apply { description = getString(R.string.notification_channel_description_messages) }

			val matchNotificationChannel =
				NotificationChannel(getString(R.string.notification_channel_id_match),
				                    getString(R.string.notification_channel_name_match),
				                    NotificationManager.IMPORTANCE_DEFAULT)
				.apply { description = getString(R.string.notification_channel_description_match) }
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			notificationManager.createNotificationChannel(messageNotificationChannel)
			notificationManager.createNotificationChannel(matchNotificationChannel)
		}

	}
}

