package com.mmdev.data.core

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
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
	fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

	@Provides
	@Singleton
	fun firebaseDatabase(): FirebaseFirestore = FirebaseFirestore.getInstance()

	@Provides
	@Singleton
	fun firebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}