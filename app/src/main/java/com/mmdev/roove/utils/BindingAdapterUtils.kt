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
	@BindingAdapter("app:photoUrl")
	fun loadPhotoUrl(imageView: ImageView, url: String?) {
		if (!url.isNullOrEmpty())
			Glide.with(imageView.context)
				.load(url)
				.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
				.into(imageView)
	}


}
