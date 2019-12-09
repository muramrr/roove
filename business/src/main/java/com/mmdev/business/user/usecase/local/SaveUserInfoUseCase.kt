/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user.usecase.local

import com.mmdev.business.user.entity.UserItem
import com.mmdev.business.user.repository.UserRepository

/**
 * This is the documentation block about the class
 */

class SaveUserInfoUseCase (private val repository: UserRepository.LocalUserRepository) {

	fun execute(currentUserItem: UserItem) = repository.saveUserInfo(currentUserItem)

}