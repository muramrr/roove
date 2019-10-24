package com.mmdev.business.feed.usecase


import com.mmdev.business.feed.repository.FeedRepository

class GetFeedItemUseCase(private val repository: FeedRepository) {

	fun execute(feedId: String) = repository.getFeedItem(feedId)

}