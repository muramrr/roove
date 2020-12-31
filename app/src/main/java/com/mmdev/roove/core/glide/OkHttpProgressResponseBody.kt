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

import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Source
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

	override fun contentType(): MediaType? {
		return responseBody.contentType()
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