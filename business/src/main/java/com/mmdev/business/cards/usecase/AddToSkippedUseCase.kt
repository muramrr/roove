/*
 * Created by Andrii Kovalchuk on 17.09.19 14:07
 * Copyright (c) 2019. All rights reserved.
 * Last modified 01.11.19 20:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.cards.usecase

import com.mmdev.business.cards.model.CardItem
import com.mmdev.business.cards.repository.CardsRepository

/**
 * This is the documentation block about the class
 */

class AddToSkippedUseCase (private val repository: CardsRepository)  {

	fun execute(t: CardItem) = repository.addToSkipped(t)

}