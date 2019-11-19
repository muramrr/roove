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
import com.mmdev.business.user.model.UserItem
import com.mmdev.business.user.usecase.local.GetSavedUserUseCase
import com.mmdev.business.user.usecase.local.SaveUserInfoUseCase

class LocalUserRepoVM (private val getSavedUser: GetSavedUserUseCase,
                       private val saveUserInfo: SaveUserInfoUseCase): ViewModel() {

	fun getSavedUser() = getSavedUser.execute()
	fun saveUserInfo(currentUserItem: UserItem) = saveUserInfo.execute(currentUserItem)

}
