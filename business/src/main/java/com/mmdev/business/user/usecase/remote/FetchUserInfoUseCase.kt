/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 19.12.19 21:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user.usecase.remote

import com.mmdev.business.user.UserItem
import com.mmdev.business.user.repository.RemoteUserRepository


/**
 * This is the documentation block about the class
 */

class FetchUserInfoUseCase (private val repository: RemoteUserRepository) {

	fun execute(u: UserItem) = repository.fetchUserInfo(u)

}