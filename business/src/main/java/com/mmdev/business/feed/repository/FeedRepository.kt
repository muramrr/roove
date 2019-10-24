package com.mmdev.business.feed.repository

import com.mmdev.business.feed.model.FeedItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/* Created by A on 12.10.2019.*/

/**
 * This is the documentation block about the class
 */

interface FeedRepository {

	fun getFeedList(): Observable<List<FeedItem>>

	fun getFeedItem(feedId: String): Single<FeedItem>

	fun postFeedItem(feedItem: FeedItem): Completable

}