/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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
		
		val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		// Since android Oreo notification channel is needed.
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			
			// Create the NotificationChannel
			val messageNotificationChannel =
				NotificationChannel(
					getString(R.string.notification_channel_id_messages),
					getString(R.string.notification_channel_name_messages),
					NotificationManager.IMPORTANCE_DEFAULT
				).apply { description = getString(R.string.notification_channel_description_messages) }

			val matchNotificationChannel =
				NotificationChannel(
					getString(R.string.notification_channel_id_match),
					getString(R.string.notification_channel_name_match),
					NotificationManager.IMPORTANCE_DEFAULT
				).apply { description = getString(R.string.notification_channel_description_match) }
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			notificationManager.createNotificationChannel(messageNotificationChannel)
			notificationManager.createNotificationChannel(matchNotificationChannel)
		}

	}
}

