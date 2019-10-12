package com.mmdev.data.feed

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.feed.model.FeedItem
import com.mmdev.business.feed.repository.FeedRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/* Created by A on 12.10.2019.*/

/**
 * This is the documentation block about the class
 */

class FeedRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore): FeedRepository {

	override fun getFeedList(): Observable<List<FeedItem>> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getFeedItem(feedId: String): Single<FeedItem> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun postFeedItem(feedItem: FeedItem): Completable {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}