/*
 * Created by Andrii Kovalchuk on 21.11.19 21:02
 * Copyright (c) 2019. All rights reserved.
 * Last modified 21.11.19 20:03
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


object BindingAdapterUtils {

	@JvmStatic
	@BindingAdapter("android:src")
	fun loadImage(imageView: ImageView, url: Int?) {
		if (url != null)
			Glide.with(imageView.context)
				.load(url)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.into(imageView)
	}

	@JvmStatic
	@BindingAdapter("app:circleImage")
	fun loadCircleImage(imageView: ImageView, url: String?) {
		if (!url.isNullOrEmpty())
			Glide.with(imageView.context)
				.load(url)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
				.into(imageView)
	}

	@JvmStatic
	@BindingAdapter("app:imageUrl")
	fun loadPhotoUrl(imageView: ImageView, url: String?) {
		if (!url.isNullOrEmpty())
			Glide.with(imageView.context)
				.load(url)
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
				.into(imageView)
	}


}
