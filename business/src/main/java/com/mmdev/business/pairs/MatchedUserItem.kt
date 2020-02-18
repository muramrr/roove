/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 18.02.20 17:33
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.pairs

import com.mmdev.business.user.UserItem
import java.util.*

data class MatchedUserItem(val userItem: UserItem = UserItem(),
                           val conversationStarted: Boolean = false,
                           var conversationId: String = "",
                           val matchedDate: Date = Date())