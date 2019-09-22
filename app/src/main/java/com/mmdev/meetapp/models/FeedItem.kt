package com.mmdev.meetapp.models

data class FeedItem (var feedPublisherName: String, var feedPublisherPhotoId: Int, var feedType: String,
                     var feedSharedTime: String,
                     var feedContentImageView: Int, var feedContentDescription: String, var leftAvailSlots: Int,
                     var feedLikesCount: Int,
                     var feedCommentsCount: Int, var feedSharesCount: Int, var liked: Boolean)
