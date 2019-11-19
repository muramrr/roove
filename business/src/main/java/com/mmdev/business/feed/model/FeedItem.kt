/*
 * Created by Andrii Kovalchuk on 12.10.19 16:52
 * Copyright (c) 2019. All rights reserved.
 * Last modified 28.10.19 18:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.feed.model

import com.mmdev.business.chat.model.PhotoAttachementItem
import com.mmdev.business.user.model.UserItem
import java.util.*

/**
 * This is the documentation block about the class
 */

data class FeedItem(var description: String = "",
                    var title: String = "",
                    var interested: Int = 0,
                    var views: Int = 0,
                    var category: ArrayList<String>? = null,
                    var sender: UserItem = UserItem(),
                    val timestamp: Date? = Date(),
                    var customPhotoAttachementItem: PhotoAttachementItem? = PhotoAttachementItem())