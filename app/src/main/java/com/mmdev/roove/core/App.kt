package com.mmdev.roove.core

import android.app.Application
import com.mmdev.data.core.AuthModule
import com.mmdev.data.core.FirebaseModule
import com.mmdev.data.core.RepositoryModule
import com.mmdev.data.core.UserModule


class App : Application() {

	override fun onCreate() {
		super.onCreate()
		injector = DaggerAppComponent.builder()
			.appModule(AppModule(applicationContext))
			.authModule(AuthModule())
			.firebaseModule(FirebaseModule())
			.repositoryModule(RepositoryModule())
			.viewModelModule(ViewModelModule())
			.userModule(UserModule())
			.build()
	}
}

lateinit var injector: AppComponent