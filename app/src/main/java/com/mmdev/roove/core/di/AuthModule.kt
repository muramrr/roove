package com.mmdev.roove.core.di

import com.facebook.login.LoginManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/* Created by A on 26.08.2019.*/


@Module
class AuthModule{

	@Provides
	@Singleton
	fun facebookAuth(): LoginManager = LoginManager.getInstance()
	
}