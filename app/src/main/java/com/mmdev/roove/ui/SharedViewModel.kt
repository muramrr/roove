/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 05.12.19 19:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.events.model.EventItem
import com.mmdev.business.user.model.UserItem

/**
 * This is the documentation block about the class
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
	}

	fun setPlaceSelected(placeItem: EventItem){
		this.placeSelected.value = placeItem
	}



}