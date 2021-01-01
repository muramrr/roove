/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.business.user

import com.mmdev.business.data.PhotoItem
import com.mmdev.business.places.BasePlaceInfo

data class UserItem(
    val baseUserInfo: BaseUserInfo = BaseUserInfo(),
    var cityToDisplay: String = "",
    var aboutText: String = "",
    var photoURLs: List<PhotoItem> = listOf(),
    val placesToGo: MutableList<BasePlaceInfo> = mutableListOf(),
    val preferredAgeRange: PreferredAgeRange = PreferredAgeRange()
) {

    fun clone() = UserItem(baseUserInfo, cityToDisplay, aboutText, photoURLs, placesToGo, preferredAgeRange)

}

data class PreferredAgeRange (var minAge: Int = 18, var maxAge: Int = 18)