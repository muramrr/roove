package com.mmdev.roove.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object BindingAdapterUtils {

	@JvmStatic
	@BindingAdapter("android:src")
	fun loadImage(imageView: ImageView, url: String?) {
		if (!url.isNullOrEmpty())
			Glide.with(imageView.context)
				.load(url)
				.into(imageView)
	}

	@JvmStatic
	@BindingAdapter("app:circleImage")
	fun loadCircleImage(imageView: ImageView, url: Int?) {
		if (url != null)
			Glide.with(imageView.context)
				.load(url)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.into(imageView)
	}

}
