/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.02.20 20:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.pairs.usecase

import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.pairs.PairsRepository

/**
 * This is the documentation block about the class
 */

class DeleteMatchUseCase (private val repository: PairsRepository)  {

	fun execute(t: MatchedUserItem) = repository.deleteMatchedUser(t)

}