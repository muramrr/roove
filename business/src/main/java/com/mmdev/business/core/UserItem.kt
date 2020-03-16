/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.03.20 14:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.core

import com.mmdev.business.places.BasePlaceInfo

data class UserItem(val baseUserInfo: BaseUserInfo = BaseUserInfo(),
                    var cityToDisplay: String = "",
                    var aboutText: String = "",
                    var photoURLs: MutableList<PhotoItem> = mutableListOf(),
                    val placesToGo: MutableList<BasePlaceInfo> = mutableListOf(),
                    val preferredAgeRange: PreferredAgeRange = PreferredAgeRange()) {

    fun clone() = UserItem(baseUserInfo, cityToDisplay, aboutText, photoURLs, placesToGo, preferredAgeRange)

}

data class PreferredAgeRange (var minAge: Int = 18, var maxAge: Int = 18)