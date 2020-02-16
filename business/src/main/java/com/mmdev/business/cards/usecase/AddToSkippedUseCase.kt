/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.02.20 17:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.cards.usecase

import com.mmdev.business.cards.repository.CardsRepository
import com.mmdev.business.user.UserItem

/**
 * This is the documentation block about the class
 */

class AddToSkippedUseCase (private val repository: CardsRepository)  {

	fun execute(t: UserItem) = repository.addToSkipped(t)

}