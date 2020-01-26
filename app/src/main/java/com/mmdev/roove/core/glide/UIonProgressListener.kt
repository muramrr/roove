/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.01.20 14:43
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.glide

/**
 * this interface is responsible for updating the UI,
 * which updates the progress of the ProgressBar
 *
 * @param granularityPercentage controls how often the listener needs an update.
 * 0% and 100% will always be dispatched.
 * For example, if you return one, it will dispatch at most 100 times,
 * with each time representing at least one percent of the progress.
 */

interface UIonProgressListener {

	val granularityPercentage: Float

	fun onProgress(bytesRead: Long, expectedLength: Long)
}