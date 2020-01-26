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

import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

/**
 * Take the original ResponseBody of OkHttp and return a new ResponseBody,
 * which has your listener embedded in.
 * Create your own BufferedSource to read the downloaded byte length.
 * Nothing special here. Just return the content type of the original ResponseBody.
 * Return the content length of the original ResponseBody.
 * Recreate the BufferedSource of your own with source(source: Source): Source below.
 * This is the key part of the progress listening section.
 * This method returns a new Source, which keeps track of totalBytesRead and dispatches it to the listener.
 */

class OkHttpProgressResponseBody internal constructor(
	private val url: HttpUrl,
	private val responseBody: ResponseBody,
	private val progressListener: ResponseProgressListener) : ResponseBody() {

	private var bufferedSource: BufferedSource? = null

	override fun contentType(): MediaType {
		return responseBody.contentType()!!
	}


	override fun contentLength(): Long {
		return responseBody.contentLength()
	}


	override fun source(): BufferedSource {
		if (bufferedSource == null) {
			bufferedSource = Okio.buffer(source(responseBody.source()))
		}
		return this.bufferedSource!!
	}


	private fun source(source: Source): Source {
		return object : ForwardingSource(source) {
			var totalBytesRead = 0L

			@Throws(IOException::class)
			override fun read(sink: Buffer, byteCount: Long): Long {
				val bytesRead = super.read(sink, byteCount)
				val fullLength = responseBody.contentLength()
				if (bytesRead.toInt() == -1) { // this source is exhausted
					totalBytesRead = fullLength
				} else {
					totalBytesRead += bytesRead
				}
				progressListener.update(url, totalBytesRead, fullLength)
				return bytesRead
			}
		}
	}
}