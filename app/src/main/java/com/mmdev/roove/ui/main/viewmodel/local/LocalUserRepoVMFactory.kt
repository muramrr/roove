/*
 * Created by Andrii Kovalchuk on 02.11.19 19:30
 * Copyright (c) 2019. All rights reserved.
 * Last modified 12.11.19 20:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main.viewmodel.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.user.usecase.local.GetSavedUserUseCase
import com.mmdev.business.user.usecase.local.SaveUserInfoUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class LocalUserRepoVMFactory @Inject constructor(private val getSavedUser: GetSavedUserUseCase,
                                                 private val saveUserInfo: SaveUserInfoUseCase) :
		ViewModelProvider.Factory {

	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(LocalUserRepoVM::class.java)) {
			return LocalUserRepoVM(getSavedUser,
			                       saveUserInfo) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}