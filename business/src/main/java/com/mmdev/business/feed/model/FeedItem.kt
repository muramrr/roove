package com.mmdev.business.feed.model

import com.mmdev.business.chat.model.PhotoAttached
import com.mmdev.business.user.model.User
import java.util.*

/* Created by A on 12.10.2019.*/

/**
 * This is the documentation block about the class
 */

data class FeedItem(var description: String = "",
                    var title: String = "",
                    var interested: Int = 0,
                    var views: Int = 0,
                    var category: ArrayList<String>? = null,
                    var sender: User = User(),
                    val timestamp: Date? = Date(),
                    var customPhotoAttached: PhotoAttached? = PhotoAttached())