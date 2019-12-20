/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.12.19 17:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations

import com.mmdev.business.base.BaseUserInfo
import java.util.*

/**
 * This is the documentation block about the class
 */

data class ConversationItem(val partner: BaseUserInfo = BaseUserInfo(),
                            val conversationId: String = "",
                            val conversationStarted: Boolean = false,
                            val lastMessageText: String = "",
                            val lastMessageTimestamp: Date? = Date())