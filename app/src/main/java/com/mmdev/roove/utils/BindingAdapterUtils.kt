/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 26.01.20 19:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import com.mmdev.roove.core.glide.GlideImageLoader


object BindingAdapterUtils {

	@JvmStatic
	@BindingAdapter("app:bindLoadingImage")
	fun loadImage(imageView: ImageView, show: Boolean) {
		if (show)
			GlideApp.with(imageView.context)
				.asGif()
				.load(R.drawable.loading)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
				.into(imageView)
	}

	@JvmStatic
	@BindingAdapter("app:bindCircleImageUrl")
	fun loadCircleImage(imageView: ImageView, url: String?) {
		if (!url.isNullOrEmpty())
			GlideApp.with(imageView.context)
				.load(url)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
				.placeholder(R.drawable.placeholder_profile)
				.into(imageView)
	}

	@JvmStatic
	@BindingAdapter(value = ["app:bindImageUrl", "app:progressBar"], requireAll = false)
	fun loadPhotoUrl(imageView: ImageView, url: String?, progressBar: ProgressBar?) {
		if (!url.isNullOrEmpty())
			GlideImageLoader(imageView, progressBar)
				.load(url, RequestOptions()
					.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
					.error(R.drawable.placeholder_image))
	}

}
