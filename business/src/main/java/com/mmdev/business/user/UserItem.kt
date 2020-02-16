/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.02.20 15:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user

import com.mmdev.business.places.BasePlaceInfo

data class UserItem(val baseUserInfo: BaseUserInfo = BaseUserInfo(),
                    val photoURLs: MutableList<String> = mutableListOf(),
                    val placesToGo: MutableList<BasePlaceInfo> = mutableListOf())




