/*
 * Created by Andrii Kovalchuk on 01.09.19 23:41
 * Copyright (c) 2019. All rights reserved.
 * Last modified 02.10.19 18:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.auth.usecase

import com.mmdev.business.auth.repository.AuthRepository

/**
 * This is the documentation block about the class
 */

class IsAuthenticatedUseCase (private val repository: AuthRepository) {

	fun execute() = repository.isAuthenticated()

}