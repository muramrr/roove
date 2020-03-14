/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.03.20 17:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import com.mmdev.business.core.UserItem
import javax.inject.Singleton


/**
 * wrapper current user to use as instance in singleton classes that requires actual user info
 */

@Singleton
class UserWrapper {

	private var user: UserItem = UserItem()

	fun getUser(): UserItem = user

	fun setUser(user: UserItem) {
		this.user = user
	}
}