/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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
import com.mmdev.data.repository.auth.AuthFlowProvider
import com.mmdev.domain.conversations.ConversationItem
import com.mmdev.domain.pairs.MatchedUserItem
import com.mmdev.domain.user.UserState
import com.mmdev.domain.user.data.UserItem
import com.mmdev.roove.core.log.logError
import com.mmdev.roove.ui.common.base.BaseViewModel
import com.mmdev.roove.ui.common.errors.ErrorType.AUTHENTICATING
import com.mmdev.roove.ui.common.errors.MyError
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit.*
import java.util.concurrent.TimeoutException
import javax.inject.Inject

/**
 * In general, you should strongly prefer passing only the minimal amount of data between destinations.
 * For example, you should pass a key to retrieve an object rather than passing the object itself,
 * as the total space for all saved states is limited on Android.
 * If you need to pass large amounts of data,
 * consider using a ViewModel as described in Share data between fragments.
 *
 * @see https://developer.android.com/guide/navigation/navigation-pass-data
 */

@HiltViewModel
class SharedViewModel @Inject constructor(
	private val authFlow: AuthFlowProvider
): BaseViewModel() {

	val matchedUserItemSelected = MutableLiveData<MatchedUserItem>()
	val userNavigateTo = MutableLiveData<UserItem>()
	val conversationSelected = MutableLiveData<ConversationItem>()

	val userState = MutableLiveData<UserState>()
	val userInfoForRegistration = MutableLiveData<UserItem>()
	
	
	fun listenUserFlow() {
		disposables.add(
			authFlow.getUserAuthState()
				.subscribe(
					{ userState.postValue(it) },
					{
						if (it is TimeoutException)
							error.postValue(
								MyError(
									AUTHENTICATING,
									Exception(
										"Timeout occurred." +
										"\nThere might be a server error or your location could not be determined." +
										"\n Take into consideration that we can't retrieve your location from GPS if you are in the building." +
										"\nPlease, enable full location access in settings."
									)
								)
							)
						else error.postValue(MyError(AUTHENTICATING, it))
					}
				)
		
		)
	}
	
	fun logOut() = authFlow.logOut()
	
	fun updateUser(user: UserItem) {
		disposables.add(
			authFlow.updateUserItem(user)
				.subscribe(
					{
						MainActivity.currentUser = user
					},
					{
						logError(TAG, "$it")
					}
				)
		)
	}

}
