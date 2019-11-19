/*
 * Created by Andrii Kovalchuk on 26.08.19 12:51
 * Copyright (c) 2019. All rights reserved.
 * Last modified 24.10.19 18:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.feed.usecase

import com.mmdev.business.feed.model.FeedItem
import com.mmdev.business.feed.repository.FeedRepository

/* Created by A on 26.08.2019.*/

class PostFeedItemUseCase (private val repository: FeedRepository) {

	fun execute(t: FeedItem) = repository.postFeedItem(t)

}

