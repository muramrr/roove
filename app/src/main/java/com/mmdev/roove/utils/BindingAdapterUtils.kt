/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 13.03.20 19:37
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.utils

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import com.mmdev.roove.core.glide.GlideImageLoader
import com.mmdev.roove.ui.common.base.BaseAdapter.BindableAdapter


object BindingAdapterUtils {


	@JvmStatic
	@BindingAdapter("app:bindData")
	fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: T) {
		if (recyclerView.adapter is BindableAdapter<*>) {
			(recyclerView.adapter as BindableAdapter<T>).setData(data)
		}
	}

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
	@BindingAdapter(value = ["app:bindImageUrl", "app:progressBar"], requireAll = false)
	fun loadPhotoUrlWithProgress(imageView: ImageView, url: String, progressBar: ProgressBar?) {
		if (url.isNotEmpty())
			if (progressBar != null) {
				GlideImageLoader(imageView, progressBar)
					.load(url,
					      RequestOptions()
						      .dontAnimate()
						      .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
						      .error(R.drawable.placeholder_image)
					)
			}
			else {
				GlideApp.with(imageView.context)
					.load(url)
					.dontAnimate()
					.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
					.into(imageView)
			}
	}

	@JvmStatic
	@BindingAdapter("app:paddingLeftSystemWindowInsets",
	                "app:paddingTopSystemWindowInsets",
	                "app:paddingRightSystemWindowInsets",
	                "app:paddingBottomSystemWindowInsets",
	                requireAll = false)
	fun applySystemWindowInsets(view: View,
	                            applyLeft: Boolean,
	                            applyTop: Boolean,
	                            applyRight: Boolean,
	                            applyBottom: Boolean) {
		view.doOnApplyWindowInsets { targetView, insets, padding ->

			val left = if (applyLeft) insets.systemWindowInsetLeft else 0
			val top = if (applyTop) insets.systemWindowInsetTop else 0
			val right = if (applyRight) insets.systemWindowInsetRight else 0
			val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

			targetView.setPadding(padding.left + left,
			                      padding.top + top,
			                      padding.right + right,
			                      padding.bottom + bottom)
		}
	}
}
