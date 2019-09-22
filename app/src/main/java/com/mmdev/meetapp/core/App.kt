package com.mmdev.meetapp.core

import android.app.Application
import com.mmdev.data.core.AuthModule
import com.mmdev.data.core.DatabaseModule
import com.mmdev.data.core.RepositoryModule
import com.mmdev.data.core.UserModule


class App : Application() {

	override fun onCreate() {
		super.onCreate()
		injector = DaggerAppComponent.builder()
			.authModule(AuthModule())
			.databaseModule(DatabaseModule())
			.repositoryModule(RepositoryModule())
			.viewModelModule(ViewModelModule())
			.userModule(UserModule(this))
			.build()
	}
}

lateinit var injector: AppComponent