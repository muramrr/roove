/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.data.datasource.UserDataSource
import com.mmdev.data.datasource.location.LocationDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *
 */

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
	
	@Provides
	@Singleton
	fun userDataSource(fs: FirebaseFirestore) = UserDataSource(fs)
	
	@Provides
	@Singleton
	fun locationDataSource(@ApplicationContext appContext: Context) = LocationDataSource(appContext)
	
	@Provides
	@Singleton
	fun appContext(@ApplicationContext appContext: Context): Context = appContext
	
}