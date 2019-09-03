package com.mmdev.domain.messages.model

import com.mmdev.domain.auth.model.User
import java.util.*

/* Created by A on 06.06.2019.*/

/* store chatmodel data class
    empty constructor needed for firebase */


data class Message (var sender: User = User(), var text: String = "", var timestamp: Date = Date(),
                    var photoAttached: PhotoAttached? = null)
