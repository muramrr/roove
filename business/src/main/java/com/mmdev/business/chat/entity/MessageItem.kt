/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.01.20 18:40
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.entity

import com.mmdev.business.base.BaseUserInfo
import java.util.*

data class MessageItem(val sender: BaseUserInfo = BaseUserInfo(),
                       val recipientId: String = "",
                       val text: String = "",
                       val timestamp: Date? = Date(),
                       val photoAttachmentItem: PhotoAttachmentItem? = PhotoAttachmentItem())