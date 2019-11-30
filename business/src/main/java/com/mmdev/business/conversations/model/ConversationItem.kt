/*
 * Created by Andrii Kovalchuk on 30.11.19 22:00
 * Copyright (c) 2019. All rights reserved.
 * Last modified 30.11.19 21:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations.model

import java.util.*

/**
 * This is the documentation block about the class
 */

data class ConversationItem(val conversationId: String = "",
                            val conversationStarted: Boolean = false,
                            val partnerId: String = "",
                            val partnerName: String = "",
                            val partnerPhotoUrl: String = "",
                            val lastMessageText: String = "",
                            val lastMessageTimestamp: Date? = Date())