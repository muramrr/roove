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