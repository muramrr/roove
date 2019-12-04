/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di.modules

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
	fun firebaseStorage() =
		FirebaseStorage.getInstance().getReferenceFromUrl(BuildConfig.FIREBASE_STORAGE_URL)
}