/*
 * Created by Andrii Kovalchuk on 23.11.19 19:40
 * Copyright (c) 2019. All rights reserved.
 * Last modified 23.11.19 18:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.main.viewmodel.local

import androidx.lifecycle.ViewModel
import com.mmdev.business.user.model.UserItem
import com.mmdev.business.user.usecase.local.GetSavedUserUseCase
import com.mmdev.business.user.usecase.local.SaveUserInfoUseCase
import javax.inject.Inject

class LocalUserRepoViewModel @Inject constructor(private val getSavedUserUC: GetSavedUserUseCase,
                                                 private val saveUserInfoUC: SaveUserInfoUseCase): ViewModel() {

	fun getSavedUser() = getSavedUserUC.execute()
	fun saveUserInfo(currentUserItem: UserItem) = saveUserInfoUC.execute(currentUserItem)

}
