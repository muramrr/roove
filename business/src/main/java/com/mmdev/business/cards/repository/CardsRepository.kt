/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 15:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.cards.repository

import com.mmdev.business.core.UserItem
import io.reactivex.rxjava3.core.Single


interface CardsRepository {

	fun addToSkipped(skippedUserItem: UserItem)

	fun checkMatch(likedUserItem: UserItem): Single<Boolean>

	fun getUsersByPreferences(): Single<List<UserItem>>

}
