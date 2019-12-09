/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.feed

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.feed.entity.FeedItem
import com.mmdev.business.feed.repository.FeedRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

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