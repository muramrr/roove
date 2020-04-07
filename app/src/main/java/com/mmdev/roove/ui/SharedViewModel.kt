/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 13:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
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
