/*
 * Created by Andrii Kovalchuk on 26.11.19 20:29
 * Copyright (c) 2019. All rights reserved.
 * Last modified 26.11.19 18:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.cards.usecase

import com.mmdev.business.cards.repository.CardsRepository

/**
 * This is the documentation block about the class
 */

class GetUsersByPreferencesUseCase (private val repository: CardsRepository) {

	fun execute() = repository.getUsersByPreferences()
}