/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:21
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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mmdev.data.core.log.DebugConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.di.AppComponent
import com.mmdev.roove.core.di.DaggerAppComponent
import dagger.hilt.android.HiltAndroidApp


lateinit var injector: AppComponent

@HiltAndroidApp
class RooveApp : Application() {
	
	companion object {
		val debug: DebugConfig = DebugConfig.Default
	}

	override fun onCreate() {
		super.onCreate()
		FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!debug.isEnabled)
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

