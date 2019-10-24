package com.mmdev.business.chat.model

import com.mmdev.business.user.model.User
import java.util.*

/* Created by A on 06.06.2019.*/

/* store chatmodel data class
    empty constructor needed for firebase */


data class Message (var sender: User = User(),
                    var text: String = "",
                    val timestamp: Date? = Date(),
                    var photoAttached: PhotoAttached? = PhotoAttached())