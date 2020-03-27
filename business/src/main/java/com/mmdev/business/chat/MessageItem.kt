/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.03.20 16:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat

import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.PhotoItem

data class MessageItem(val sender: BaseUserInfo = BaseUserInfo(),
                       val recipientId: String = "",
                       val text: String = "",
                       var timestamp: Any? = null,
                       val photoItem: PhotoItem? = PhotoItem(),
                       val conversationId: String = "")