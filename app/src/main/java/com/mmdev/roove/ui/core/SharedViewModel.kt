/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 08.12.19 20:16
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.events.model.EventItem
import com.mmdev.business.user.model.UserItem

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

	val placeSelected: MutableLiveData<EventItem> = MutableLiveData()

	val currentUser: MutableLiveData<UserItem> = MutableLiveData()



	fun setCurrentUser(userItem: UserItem){
		currentUser.value = userItem
	}

	fun setCardSelected(cardItem: CardItem){
		cardSelected.value = cardItem
	}

	fun setConversationSelected(conversationItem: ConversationItem){
		conversationSelected.value = conversationItem
		//if we moved to user profile from conversation
		if (cardSelected.value?.userId != conversationItem.partnerId)
			Log.wtf("mylogs", "card updated")
			cardSelected.value = CardItem(name = conversationItem.partnerName,
			                              mainPhotoUrl = conversationItem.partnerPhotoUrl,
			                              userId = conversationItem.partnerId,
			                              conversationStarted = conversationItem.conversationStarted)
	}

	fun setPlaceSelected(placeItem: EventItem){
		this.placeSelected.value = placeItem
	}



}