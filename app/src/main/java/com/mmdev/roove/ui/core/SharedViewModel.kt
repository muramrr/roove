/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.01.20 18:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.CardItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.user.UserItem

/**
 * In general, you should strongly prefer passing only the minimal amount of data between destinations.
 * For example, you should pass a key to retrieve an object rather than passing the object itself,
 * as the total space for all saved states is limited on Android.
 * If you need to pass large amounts of data,
 * consider using a ViewModel as described in Share data between fragments.
 *
 * @see https://developer.android.com/guide/navigation/navigation-pass-data
 */

class SharedViewModel: ViewModel() {


	val cardSelected: MutableLiveData<CardItem> = MutableLiveData()

	val conversationSelected: MutableLiveData<ConversationItem> = MutableLiveData()


	private val currentUser: MutableLiveData<UserItem> = MutableLiveData()

	fun getCurrentUser() = currentUser

	fun setCurrentUser(userItem: UserItem){
		currentUser.value = userItem
	}

	fun setCardSelected(cardItem: CardItem){
		cardSelected.value = cardItem
	}

	fun setConversationSelected(conversationItem: ConversationItem){
		conversationSelected.value = conversationItem
		//if we moved to user profile from conversation
		if (cardSelected.value?.baseUserInfo?.userId != conversationItem.partner.userId) {
			Log.wtf("mylogs_SharedViewModel", "card updated")
			cardSelected.value = CardItem(conversationItem.partner,
			                              conversationStarted = conversationItem.conversationStarted)
		}
	}



}