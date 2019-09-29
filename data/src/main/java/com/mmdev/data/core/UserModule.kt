package com.mmdev.data.core

import android.content.Context
import com.mmdev.data.user.UserRepositoryImpl
import com.mmdev.domain.core.model.User
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/* Created by A on 16.09.2019.*/

/**
 * This is the documentation block about the class
 */

@Module
class UserModule (private val context: Context) {


	@Provides
	@Singleton
	fun provideContext(): Context { return context }


	/* get user info from sharedPrefs */
	@Provides
	@Singleton
	fun provideSavedUser(repositoryImpl: UserRepositoryImpl): User = repositoryImpl.getSavedUser()

}