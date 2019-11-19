/*
 * Created by Andrii Kovalchuk on 06.06.19 15:21
 * Copyright (c) 2019. All rights reserved.
 * Last modified 31.10.19 20:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.chat.model

import com.mmdev.business.user.model.UserItem
import java.util.*

data class MessageItem (var sender: UserItem = UserItem(),
                        var text: String = "",
                        val timestamp: Date? = Date(),
                        var photoAttachementItem: PhotoAttachementItem? = PhotoAttachementItem())