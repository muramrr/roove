/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 19.12.19 21:21
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.drawerflow.viewmodel.local

import androidx.lifecycle.ViewModel
import com.mmdev.business.user.UserItem
import com.mmdev.business.user.usecase.local.GetSavedUserUseCase
import com.mmdev.business.user.usecase.local.SaveUserInfoUseCase
import javax.inject.Inject

class LocalUserRepoViewModel @Inject constructor(private val getSavedUserUC: GetSavedUserUseCase,
                                                 private val saveUserInfoUC: SaveUserInfoUseCase):
		ViewModel() {



	fun getSavedUser() = getSavedUserExecution()
	fun saveUserInfo(currentUserItem: UserItem) = saveUserInfoExecution(currentUserItem)

	private fun getSavedUserExecution() = getSavedUserUC.execute()
	private fun saveUserInfoExecution(currentUserItem: UserItem) =
		saveUserInfoUC.execute(currentUserItem)

}
