/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.01.20 14:39
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.glide

import okhttp3.HttpUrl

/**
 * This interface is responsible for notifying
 * whoever is listening to the downloading progress of the URL.
 */

interface ResponseProgressListener {
	fun update(url: HttpUrl, bytesRead: Long, contentLength: Long)
}