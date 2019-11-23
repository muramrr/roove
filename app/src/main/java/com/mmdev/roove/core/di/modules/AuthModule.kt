/*
 * Created by Andrii Kovalchuk on 23.11.19 19:40
 * Copyright (c) 2019. All rights reserved.
 * Last modified 23.11.19 17:47
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