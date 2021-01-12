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

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target


/**
 * This is the documentation block about the class
 */

class GlideImageLoader(private val mImageView: ImageView,
                       private val mProgressBar: ProgressBar?) {

	fun load(url: String?, options: RequestOptions?) {
		if (options == null) return

		onConnecting()

		DispatchingProgressManager.expect(url, object : UIonProgressListener {

			override val granularityPercentage: Float
				get() = 1.0f

			override fun onProgress(bytesRead: Long, expectedLength: Long) {
				if (mProgressBar != null) {
					mProgressBar.progress = (100 * bytesRead / expectedLength).toInt()
				}
			}
		})

		GlideApp.with(mImageView.context)
			.load(url)
			.apply(options)
			.listener(object : RequestListener<Drawable> {

				override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
				                          isFirstResource: Boolean): Boolean {
					DispatchingProgressManager.forget(url)
					onFinished()
					return false
				}

				override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
				                             dataSource: DataSource?, isFirstResource: Boolean): Boolean {

					DispatchingProgressManager.forget(url)
					onFinished()
					return false
				}
			})
			.into(mImageView)
	}


	private fun onConnecting() {
		if (mProgressBar != null) mProgressBar.visibility = View.VISIBLE
	}

	private fun onFinished() {
		if (mProgressBar != null) {
			mProgressBar.visibility = View.GONE
			mImageView.visibility = View.VISIBLE
		}
	}
}