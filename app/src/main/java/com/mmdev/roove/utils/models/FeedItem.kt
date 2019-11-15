package com.mmdev.roove.utils.models

data class FeedItem (val feedPublisherName: String,
                     val feedPublisherPhotoId: Int,
                     val feedType: String,
                     val feedSharedTime: String,
                     val feedContentImageView: String,
                     val feedContentDescription: String,
                     var leftAvailSlots: Int)
