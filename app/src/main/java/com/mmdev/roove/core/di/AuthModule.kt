/*
 * Created by Andrii Kovalchuk on 19.09.19 12:18
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.11.19 20:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.di

import com.facebook.login.LoginManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule{

	@Provides
	@Singleton
	fun facebookAuth(): LoginManager = LoginManager.getInstance()
	
}