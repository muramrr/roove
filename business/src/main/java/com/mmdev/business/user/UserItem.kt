/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.12.19 18:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user

import com.mmdev.business.base.BaseUserInfo

data class UserItem(val baseUserInfo: BaseUserInfo = BaseUserInfo(),
                    var preferredGender: String = "",
                    val photoURLs: List<String> = listOf())




