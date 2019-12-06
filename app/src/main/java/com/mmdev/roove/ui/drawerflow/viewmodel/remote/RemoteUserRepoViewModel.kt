/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 06.12.19 17:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.drawerflow.viewmodel.remote

import androidx.lifecycle.ViewModel
import com.mmdev.business.user.usecase.remote.CreateUserUseCase
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.GetUserByIdUseCase
import javax.inject.Inject

/**
 * This is the documentation block about the class
 */

class RemoteUserRepoViewModel @Inject constructor(private val createUserUC: CreateUserUseCase,
                                                  private val deleteUserUC: DeleteUserUseCase,
                                                  private val getUserUC: GetUserByIdUseCase) :
		ViewModel() {

	fun createUser() = createUserUC.execute()

	fun deleteUser(userId: String) = deleteUserUC.execute(userId)

	fun getUserById(userId: String) = getUserUC.execute(userId)


}