/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 29.01.20 16:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.cards

import com.mmdev.business.user.BaseUserInfo

data class CardItem(val baseUserInfo: BaseUserInfo = BaseUserInfo(),
                    val conversationStarted: Boolean = false)