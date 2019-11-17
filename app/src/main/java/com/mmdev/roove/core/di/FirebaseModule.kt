package com.mmdev.roove.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mmdev.data.BuildConfig
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/* Created by A on 25.10.2019.*/

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