package com.mmdev.roove.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapterUtils {

	@JvmStatic
	@BindingAdapter("android:src")
	fun loadImage(imageView: ImageView, url: String?) {
		if (!url.isNullOrEmpty()) Glide.with(imageView.context).load(url).into(imageView)
	}

}
