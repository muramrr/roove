/*
 * Created by Andrii Kovalchuk on 10.11.19 21:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 12.11.19 20:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main.viewmodel.remote

import androidx.lifecycle.ViewModel
import com.mmdev.business.user.usecase.remote.CreateUserUseCase
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.GetUserByIdUseCase

/**
 * This is the documentation block about the class
 */

class RemoteUserRepoVM (private val createUserUC: CreateUserUseCase,
                        private val deleteUserUC: DeleteUserUseCase,
                        private val getUserUC: GetUserByIdUseCase) : ViewModel() {

	fun createUser() = createUserUC.execute()

	fun deleteUser(userId: String) = deleteUserUC.execute(userId)

	fun getUserById(userId: String) = getUserUC.execute(userId)


}