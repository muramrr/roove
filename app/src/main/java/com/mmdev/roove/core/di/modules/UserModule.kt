/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.roove.core.di.modules

import android.app.Application
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import com.ironz.binaryprefs.encryption.AesValueEncryption
import com.ironz.binaryprefs.encryption.XorKeyEncryption
import com.mmdev.business.local.LocalUserRepository
import com.mmdev.data.BuildConfig
import com.mmdev.data.repository.user.UserWrapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class UserModule {

	@Provides
	@Singleton
	fun provideUserWrapper(localRepo: LocalUserRepository): UserWrapper = UserWrapper(localRepo)

	@Provides
	@Singleton
	fun binaryPrefs(context: Application): Preferences =
		BinaryPreferencesBuilder(context).apply {
			keyEncryption(XorKeyEncryption(BuildConfig.KEY_ENCRYPTION_KEY.toByteArray()))
			valueEncryption(AesValueEncryption(BuildConfig.VALUE_ENCRYPTION_KEY.toByteArray(),
			                                   BuildConfig.VALUE_ENCRYPTION_VECTOR_KEY.toByteArray()))
		}.build()

}