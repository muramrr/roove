package com.mmdev.meetapp.core

import android.app.Application
import com.mmdev.data.core.DatabaseModule
import com.mmdev.data.core.RepositoryModule


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        injector = DaggerAppComponent.builder()
                .databaseModule(DatabaseModule())
                .repositoryModule(RepositoryModule())
                .viewModelModule(ViewModelModule())
                .build()
    }
}

lateinit var injector: AppComponent