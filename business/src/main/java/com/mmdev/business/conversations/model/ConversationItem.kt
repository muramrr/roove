/*
 * Created by Andrii Kovalchuk on 26.10.19 16:49
 * Copyright (c) 2019. All rights reserved.
 * Last modified 30.10.19 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.conversations.model

/**
 * This is the documentation block about the class
 */

data class ConversationItem(val conversationId: String = "",
                            val partnerId: String = "",
                            val partnerName: String = "",
                            val partnerPhotoUrl: String = "",
                            val lastMessageText: String = "")