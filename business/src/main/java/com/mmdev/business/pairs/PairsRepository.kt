/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.02.20 17:10
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.pairs

import com.mmdev.business.cards.MatchedUserItem
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface PairsRepository {

	fun getMatchedUsersList(): Single<List<MatchedUserItem>>

}