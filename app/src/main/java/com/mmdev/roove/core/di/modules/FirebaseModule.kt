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

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mmdev.data.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Core firebase components, I think @Singleton annotation here is useless due to .getInstance()
 * returns singletons by itself
 */

@Module
@InstallIn(SingletonComponent::class)
class FirebaseModule {

	@Provides
	@Singleton
	fun firebaseAuth() = FirebaseAuth.getInstance()

	@Provides
	@Singleton
	fun firebaseDatabase() = FirebaseFirestore.getInstance()

	@Provides
	@Singleton
	fun firebaseInstance() = FirebaseInstallations.getInstance()

	@Provides
	@Singleton
	fun firebaseStorage(): StorageReference {
		val storage = FirebaseStorage.getInstance()
		storage.maxDownloadRetryTimeMillis = 60000  // wait 1 min for downloads
		storage.maxOperationRetryTimeMillis = 10000  // wait 10s for normal ops
		storage.maxUploadRetryTimeMillis = 120000  // wait 2 mins for uploads
		return storage.getReferenceFromUrl(BuildConfig.FIREBASE_STORAGE_URL)
	}

}