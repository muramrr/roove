package com.mmdev.business.feed.usecase


import com.mmdev.business.core.usecase.ObservableUseCase
import com.mmdev.business.feed.model.FeedItem
import com.mmdev.business.feed.repository.FeedRepository

class GetFeedListUseCase (private val repository: FeedRepository) :
		ObservableUseCase<List<FeedItem>> {

    override fun execute() = repository.getFeedList()
}