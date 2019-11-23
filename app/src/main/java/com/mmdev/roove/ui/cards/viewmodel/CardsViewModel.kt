/*
 * Created by Andrii Kovalchuk on 23.11.19 19:40
 * Copyright (c) 2019. All rights reserved.
 * Last modified 23.11.19 18:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards.viewmodel

import androidx.lifecycle.ViewModel
import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetMatchedUsersUseCase
import com.mmdev.business.cards.usecase.GetPotentialUsersUseCase
import com.mmdev.business.cards.usecase.HandlePossibleMatchUseCase
import javax.inject.Inject

class CardsViewModel @Inject constructor(private val addToSkippedUC: AddToSkippedUseCase,
                                        private val getMatchedUsersUC: GetMatchedUsersUseCase,
                                        private val getPotentialUsersUC: GetPotentialUsersUseCase,
                                        private val handlePossibleMatchUC: HandlePossibleMatchUseCase):
		ViewModel(){

	fun addToSkipped(skippedCardItem: CardItem) = addToSkippedUC.execute(skippedCardItem)
	fun getMatchedUserItems() = getMatchedUsersUC.execute()
	fun getPotentialUserCards() = getPotentialUsersUC.execute()
	fun handlePossibleMatch(likedCardItem: CardItem) = handlePossibleMatchUC.execute(likedCardItem)

}

