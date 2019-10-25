package com.mmdev.data.core

import com.mmdev.business.user.model.User
import com.mmdev.data.user.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/* Created by A on 16.09.2019.*/

/**
 * This is the documentation block about the class
 */

@Module
class UserModule {

	/* get user info from sharedPrefs */
	@Provides
	@Singleton
	fun provideSavedUser(repository: UserRepositoryImpl): User = repository.getSavedUser()

}