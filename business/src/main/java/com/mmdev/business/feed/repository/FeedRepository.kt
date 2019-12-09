/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 20:46
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.feed.repository

import com.mmdev.business.feed.entity.FeedItem
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