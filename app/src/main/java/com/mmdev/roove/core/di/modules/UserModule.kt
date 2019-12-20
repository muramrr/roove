/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 19.12.19 21:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import android.app.Application
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.ironz.binaryprefs.encryption.AesValueEncryption
import com.ironz.binaryprefs.encryption.XorKeyEncryption
import com.mmdev.data.BuildConfig
import com.mmdev.data.user.UserRepositoryLocal
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UserModule {

	/* get user info from Prefs */
	@Provides
	@Singleton
	fun provideSavedUser(repository: UserRepositoryLocal) = repository.getSavedUser()

	@Provides
	@Singleton
	fun binaryPrefs(context: Application): Preferences =
		BinaryPreferencesBuilder(context).apply {
			keyEncryption(XorKeyEncryption(BuildConfig.KEY_ENCRYPTION_KEY.toByteArray()))
			valueEncryption(AesValueEncryption(BuildConfig.VALUE_ENCRYPTION_KEY.toByteArray(),
			                                   BuildConfig.VALUE_ENCRYPTION_VECTOR_KEY.toByteArray()))
		}.build()

}