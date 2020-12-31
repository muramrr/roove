/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 17:46
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
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mmdev.business.data.PhotoItem
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import com.mmdev.roove.core.glide.GlideImageLoader
import com.mmdev.roove.ui.common.base.BaseRecyclerAdapter.BindableAdapter
import com.mmdev.roove.utils.extensions.doOnApplyWindowInsets


object BindingAdapterUtils {


	@JvmStatic
	@BindingAdapter("app:visibilityInvisible")
	fun handleViewInvisibleVisibility(view: View, show: Boolean = false) {
		view.visibility = if (show) View.VISIBLE else View.INVISIBLE
	}

	@JvmStatic
	@BindingAdapter("app:visibilityGone")
	fun handleViewGoneVisibility(view: View, show: Boolean = false) {
		view.visibility = if (show) View.VISIBLE else View.GONE
	}

	@JvmStatic
	@BindingAdapter("app:bindData")
	@Suppress("UNCHECKED_CAST")
	fun <T> setRecyclerViewProperties(recyclerView: RecyclerView, data: T) {
		if (recyclerView.adapter is BindableAdapter<*>) {
			(recyclerView.adapter as BindableAdapter<T>).setData(data)
		}
	}

	@JvmStatic
	@BindingAdapter("app:bindPhotos")
	@Suppress("UNCHECKED_CAST")
	fun setViewPager2ImageAdapterProperties(viewPager2: ViewPager2, data: List<PhotoItem>) {
		(viewPager2.adapter as BindableAdapter<List<String>>).setData(data.map { it.fileUrl })
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
