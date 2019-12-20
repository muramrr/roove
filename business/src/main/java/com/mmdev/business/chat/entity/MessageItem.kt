/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.12.19 17:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.entity

import com.mmdev.business.base.BaseUserInfo
import java.util.*

data class MessageItem(var sender: BaseUserInfo = BaseUserInfo(),
                       var text: String = "",
                       val timestamp: Date? = Date(),
                       var photoAttachementItem: PhotoAttachementItem? = PhotoAttachementItem())