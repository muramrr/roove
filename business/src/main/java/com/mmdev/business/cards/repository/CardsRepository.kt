/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.02.20 17:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.cards.repository

import com.mmdev.business.cards.CardItem
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface CardsRepository {

	fun addToSkipped(skippedCardItem: CardItem)

	fun checkMatch(likedCardItem: CardItem): Single<Boolean>

	fun getUsersByPreferences(): Single<List<CardItem>>

	fun getLikedList(): Single<List<String>>

	fun getMatchedList(): Single<List<String>>

	//fun getSkippedList(): Single<List<String>>

}
