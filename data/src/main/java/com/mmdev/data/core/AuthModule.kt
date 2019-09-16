package com.mmdev.data.core

import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/* Created by A on 26.08.2019.*/

@Module
class AuthModule{

	@Provides
	@Singleton
	fun providesFirebaseAuth() = FirebaseAuth.getInstance()

	@Provides
	@Singleton
	fun providesFacebookAuth(): LoginManager = LoginManager.getInstance()

}