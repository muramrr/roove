/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.03.20 19:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.local

import com.mmdev.business.core.UserItem

/**
 * This is the documentation block about the class
 */

interface LocalUserRepository {

	fun clear()

	fun getSavedUser(): UserItem?

	fun saveUserInfo(userItem: UserItem)

}