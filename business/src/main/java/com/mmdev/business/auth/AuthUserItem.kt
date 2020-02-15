/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.02.20 18:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.auth

import com.mmdev.business.user.BaseUserInfo

/**
 * This is the documentation block about the class
 */

data class AuthUserItem(val baseUserInfo:BaseUserInfo = BaseUserInfo(),
                        val registrationTokens: MutableList<String> = mutableListOf())