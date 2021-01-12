/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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