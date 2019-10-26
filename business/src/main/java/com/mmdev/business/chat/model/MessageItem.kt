package com.mmdev.business.chat.model

import com.mmdev.business.user.model.UserItem
import java.util.*

/* Created by A on 06.06.2019.*/

/* store chatmodel data class
    empty constructor needed for firebase */


data class MessageItem (var sender: UserItem = UserItem(),
                        var text: String = "",
                        val timestamp: Date? = Date(),
                        var photoAttachementItem: PhotoAttachementItem? = PhotoAttachementItem())