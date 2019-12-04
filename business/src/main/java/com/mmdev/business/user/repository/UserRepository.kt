/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 19:13
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user.repository

import com.mmdev.business.user.model.UserItem
import io.reactivex.Completable
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */
class UserRepository {

	interface LocalUserRepository {

		fun getSavedUser(): UserItem

		fun saveUserInfo(currentUserItem: UserItem)
	}

	interface RemoteUserRepository {

		fun createUserOnRemote(): Completable

		fun deleteUser(userId: String): Completable

		fun getUserById(userId: String): Single<UserItem>

	}


}
