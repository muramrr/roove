/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.03.20 18:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mmdev.data.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Module
class FirebaseModule {

	@Provides
	@Singleton
	fun firebaseAuth() = FirebaseAuth.getInstance()

	@Provides
	@Singleton
	fun firebaseDatabase() = FirebaseFirestore.getInstance()

	@Provides
	@Singleton
	fun firebaseInstance() = FirebaseInstanceId.getInstance()

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