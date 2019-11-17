package com.mmdev.roove.core.di

import android.app.Application
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.mmdev.data.user.UserRepositoryLocal
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
	fun provideSavedUser(repository: UserRepositoryLocal) = repository.getSavedUser()

	@Provides
	@Singleton
	fun binaryPrefs(context: Application): Preferences =
		BinaryPreferencesBuilder(context).apply {
//			keyEncryption(XorKeyEncryption("JaNdRgUkXp2s5v8y/B?E(H+KbPeShVmY".toByteArray()))
//			valueEncryption(AesValueEncryption("16 bytes secret key".toByteArray(),
//			                                   "16 bytes initial vector".toByteArray()))
		}.build()

}