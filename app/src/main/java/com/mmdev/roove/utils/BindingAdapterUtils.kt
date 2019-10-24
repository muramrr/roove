package com.mmdev.roove.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapterUtils {

	@JvmStatic
	@BindingAdapter("android:src")
	fun loadImage(imageView: ImageView, id: Int) {
		Glide.with(imageView.context).load(id).into(imageView)
	}

}
