package com.mmdev.business.feed.usecase


import com.mmdev.business.core.usecase.CompletableWithParamUseCase
import com.mmdev.business.feed.model.FeedItem
import com.mmdev.business.feed.repository.FeedRepository

/* Created by A on 26.08.2019.*/

class PostFeedItemUseCase (private val repository: FeedRepository):
	CompletableWithParamUseCase<FeedItem>{

	override fun execute(t: FeedItem) = repository.postFeedItem(t)

}

