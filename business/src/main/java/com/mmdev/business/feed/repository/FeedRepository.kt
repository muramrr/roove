/*
 * Created by Andrii Kovalchuk on 12.10.19 16:51
 * Copyright (c) 2019. All rights reserved.
 * Last modified 24.10.19 18:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.feed.repository

import com.mmdev.business.feed.model.FeedItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * This is the documentation block about the class
 */

interface FeedRepository {

	fun getFeedList(): Observable<List<FeedItem>>

	fun getFeedItem(feedId: String): Single<FeedItem>

	fun postFeedItem(feedItem: FeedItem): Completable

}