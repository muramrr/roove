/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.roove.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.user.UserItem
import javax.inject.Inject

/**
 * In general, you should strongly prefer passing only the minimal amount of data between destinations.
 * For example, you should pass a key to retrieve an object rather than passing the object itself,
 * as the total space for all saved states is limited on Android.
 * If you need to pass large amounts of data,
 * consider using a ViewModel as described in Share data between fragments.
 * This [ViewModel] is used in every fragment { @see [com.mmdev.roove.ui.common.base.BaseFragment] }
 * and owner is [MainActivity]
 *
 * @see https://developer.android.com/guide/navigation/navigation-pass-data
 */

class SharedViewModel @Inject constructor(): ViewModel() {

	val matchedUserItemSelected: MutableLiveData<MatchedUserItem> = MutableLiveData()
	val userNavigateTo: MutableLiveData<UserItem> = MutableLiveData()
	val conversationSelected: MutableLiveData<ConversationItem> = MutableLiveData()

	val modalBottomSheetNeedUpdateExecution: MutableLiveData<Boolean> = MutableLiveData()

	private val currentUser: MutableLiveData<UserItem> = MutableLiveData()

	fun getCurrentUser() = currentUser
	fun setCurrentUser(userItem: UserItem) { currentUser.value = userItem }

}
