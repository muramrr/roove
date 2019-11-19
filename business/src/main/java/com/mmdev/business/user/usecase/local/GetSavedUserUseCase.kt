/*
 * Created by Andrii Kovalchuk on 29.09.19 15:24
 * Copyright (c) 2019. All rights reserved.
 * Last modified 12.11.19 20:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user.usecase.local

import com.mmdev.business.user.repository.UserRepository

/**
 * This is the documentation block about the class
 */

class GetSavedUserUseCase (private val repository: UserRepository.LocalUserRepository) {

	fun execute() = repository.getSavedUser()

}