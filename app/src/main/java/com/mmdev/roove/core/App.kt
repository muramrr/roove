package com.mmdev.roove.core

import android.app.Application


class App : Application() {

	override fun onCreate() {
		super.onCreate()
		injector = DaggerAppComponent.builder()
			.application(this)
			.build()
	}
}

lateinit var injector: AppComponent