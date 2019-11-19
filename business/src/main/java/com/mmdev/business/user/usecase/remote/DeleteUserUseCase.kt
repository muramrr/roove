/*
 * Created by Andrii Kovalchuk on 10.11.19 12:54
 * Copyright (c) 2019. All rights reserved.
 * Last modified 12.11.19 20:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user.usecase.remote

import com.mmdev.business.user.repository.UserRepository

/**
 * This is the documentation block about the class
 */

class DeleteUserUseCase (private val repository: UserRepository.RemoteUserRepository) {

	fun execute(t: String) = repository.deleteUser(t)

}