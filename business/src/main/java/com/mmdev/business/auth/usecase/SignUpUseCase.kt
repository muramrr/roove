/*
 * Created by Andrii Kovalchuk on 29.08.19 11:24
 * Copyright (c) 2019. All rights reserved.
 * Last modified 28.10.19 18:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.auth.usecase

import com.mmdev.business.auth.repository.AuthRepository
import com.mmdev.business.user.model.UserItem

class SignUpUseCase(private val repository: AuthRepository) {

    fun execute(t: UserItem) = repository.registerUser(t)

}