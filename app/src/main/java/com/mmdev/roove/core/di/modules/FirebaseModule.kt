/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 18:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
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
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * Core firebase components, I think @Singleton annotation here is useless due to .getInstance()
 * returns singletons by itself
 */

@Module
@InstallIn(ApplicationComponent::class)
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