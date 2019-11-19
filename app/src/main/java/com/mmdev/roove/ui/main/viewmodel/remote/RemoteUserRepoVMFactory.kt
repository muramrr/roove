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
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.user.usecase.remote.CreateUserUseCase
import com.mmdev.business.user.usecase.remote.DeleteUserUseCase
import com.mmdev.business.user.usecase.remote.GetUserByIdUseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Suppress("UNCHECKED_CAST")
@Singleton
class RemoteUserRepoVMFactory @Inject constructor(private val createUserUC: CreateUserUseCase,
                                                  private val deleteUserUC: DeleteUserUseCase,
                                                  private val getUserUC: GetUserByIdUseCase) :
		ViewModelProvider.Factory {

	override fun <T: ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(RemoteUserRepoVM::class.java)) {
			return RemoteUserRepoVM(createUserUC,
			                                                                 deleteUserUC,
			                                                                 getUserUC) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}