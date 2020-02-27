/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.02.20 15:53
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations

import com.mmdev.business.core.UserItem
import java.util.*

/**
 * This is the documentation block about the class
 */

data class ConversationItem(val partner: UserItem = UserItem(),
                            val conversationId: String = "",
                            val conversationStarted: Boolean = false,
                            val lastMessageText: String = "",
                            val lastMessageTimestamp: Date? = Date())