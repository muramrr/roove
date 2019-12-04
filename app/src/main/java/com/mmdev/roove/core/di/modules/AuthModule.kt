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