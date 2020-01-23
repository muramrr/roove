/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.01.20 21:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user

import com.mmdev.business.base.BasePlaceInfo
import com.mmdev.business.base.BaseUserInfo

data class UserItem(val baseUserInfo: BaseUserInfo = BaseUserInfo(),
                    var preferredGender: String = "",
                    val photoURLs: HashSet<String> = hashSetOf(),
                    val placesToGo: HashSet<BasePlaceInfo> = hashSetOf())




