package com.mmdev.roove.core

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/* Created by A on 25.10.2019.*/

/**
 * This is the documentation block about the class
 */

@Module
class AppModule (private val context: Context) {

	@Provides
	@Singleton
	fun provideContext(): Context = context

}