/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.01.20 14:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.glide

import android.os.Handler
import android.os.Looper
import okhttp3.HttpUrl

/**
 * You need a HashMap to store the progress of the URLs you want to display the ProgressBar.
 * You also need another HashMap to store the UI listeners.
 * This is the method to add the URL and its UI listener to the HashMap.
 * You call this at the beginning of the download.
 * When the download of a URL is completed or returns an error, call this to remove the URL from both HashMaps.
 * You need a UI thread handler to update the UI because the progress is notified in the background thread.
 * Not all the URLs must have a progress listener. You don't need to show ProgressBar for a photo thumbnail.
 * Yay! Download completed. Forget this URL.
 * Remember the granularityPercentage in UIonProgressListener interface?
 * This is where you decide whether to update the UI if downloaded content length is worthy; i.e., not smaller than the granularity.
 * Here is the simple explanation.
 * You get the currentProgress by dividing the current percent by the granularityPercentage value.
 * Get the lastProgress from the HashMap, and compare currentProgress and lastProgress to see
 * if there's any change; i.e., currentProgress is greater than lastProgress by the multiple of granularityPercentage.
 * If that's the case, notify UI listener.
 */

class DispatchingProgressManager: ResponseProgressListener {

	companion object {
		private val PROGRESSES = HashMap<String?, Long>()
		private val LISTENERS = HashMap<String?, UIonProgressListener>()

		internal fun expect(url: String?, listener: UIonProgressListener) {
			LISTENERS[url] = listener
		}

		internal fun forget(url: String?) {
			LISTENERS.remove(url)
			PROGRESSES.remove(url)
		}
	}

	private val handler: Handler = Handler(Looper.getMainLooper())

	override fun update(url: HttpUrl, bytesRead: Long, contentLength: Long) {
		val key = url.toString()
		val listener = LISTENERS[key] ?: return
		if (contentLength <= bytesRead) {
			forget(key)
		}
		if (needsDispatch(key, bytesRead, contentLength,
		                  listener.granularityPercentage)) {
			handler.post { listener.onProgress(bytesRead, contentLength) }
		}
	}

	private fun needsDispatch(key: String, current: Long, total: Long, granularity: Float): Boolean {
		if (granularity == 0f || current == 0L || total == current) {
			return true
		}
		val percent = 100f * current / total
		val currentProgress = (percent / granularity).toLong()
		val lastProgress = PROGRESSES[key]
		return if (lastProgress == null || currentProgress != lastProgress) {
			PROGRESSES[key] = currentProgress
			true
		} else {
			false
		}
	}
}