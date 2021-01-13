/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

package com.mmdev.roove.utils

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import com.mmdev.roove.core.glide.GlideImageLoader
import com.mmdev.roove.utils.extensions.doOnApplyWindowInsets
import com.mmdev.roove.utils.extensions.hideKeyboard


object BindingAdapterUtils {


	@JvmStatic
	@BindingAdapter("app:invisibleIf")
	fun setInvisible(view: View, condition: Boolean = false) {
		view.visibility = if (condition) View.INVISIBLE else View.VISIBLE
	}

	@JvmStatic
	@BindingAdapter("app:goneIf")
	fun setGone(view: View, condition: Boolean = false) {
		view.visibility = if (condition) View.GONE else View.VISIBLE
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
						      .error(R.color.colorPrimaryDark)
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
	
	/**
	 * Hides keyboard when the [EditText] is focused.
	 *
	 * There can only be one [TextView.OnEditorActionListener] on each [EditText] and
	 * this [BindingAdapter] sets it.
	 */
	@JvmStatic
	@BindingAdapter("app:hideKeyboardOnInputDone")
	fun hideKeyboardOnInputDone(inputView: EditText, enabled: Boolean) {
		if (!enabled) return
		val listener = TextView.OnEditorActionListener { v, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				v.text = v.text.toString().trim()
				v.hideKeyboard(v)
			}
			true
		}
		inputView.setOnEditorActionListener(listener)
	}

	@JvmStatic
	@BindingAdapter(
		"app:paddingLeftSystemWindowInsets",
		"app:paddingTopSystemWindowInsets",
		"app:paddingRightSystemWindowInsets",
		"app:paddingBottomSystemWindowInsets",
		requireAll = false
	)
	fun applySystemWindowInsets(
		view: View,
		applyLeft: Boolean,
		applyTop: Boolean,
		applyRight: Boolean,
		applyBottom: Boolean
	) {
		view.doOnApplyWindowInsets { targetView, insets, padding ->

			val left = if (applyLeft) insets.systemWindowInsetLeft else 0
			val top = if (applyTop) insets.systemWindowInsetTop else 0
			val right = if (applyRight) insets.systemWindowInsetRight else 0
			val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

			targetView.setPadding(
				padding.left + left,
				padding.top + top,
				padding.right + right,
				padding.bottom + bottom
			)
		}
	}
}
