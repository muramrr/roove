/*
 * Created by Andrii Kovalchuk on 20.07.19 16:32
 * Copyright (c) 2019. All rights reserved.
 * Last modified 30.10.19 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.cards.usecase.AddToSkippedUseCase
import com.mmdev.business.cards.usecase.GetMatchedUsersUseCase
import com.mmdev.business.cards.usecase.GetPotentialUsersUseCase
import com.mmdev.business.cards.usecase.HandlePossibleMatchUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class CardsViewModelFactory
@Inject constructor(private val addToSkippedUC: AddToSkippedUseCase,
                    private val getMatchedUsersUC: GetMatchedUsersUseCase,
                    private val getPotentialUsersUC: GetPotentialUsersUseCase,
                    private val handlePossibleMatchUC: HandlePossibleMatchUseCase) :
		ViewModelProvider.Factory {

	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		if (modelClass.isAssignableFrom(CardsViewModel::class.java)) {
			return CardsViewModel(addToSkippedUC,
			                      getMatchedUsersUC,
			                      getPotentialUsersUC,
			                      handlePossibleMatchUC) as T
		}
		throw IllegalArgumentException("Unknown ViewModel class")
	}

}