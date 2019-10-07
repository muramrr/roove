package com.mmdev.meetapp.ui.custom


import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window.FEATURE_NO_TITLE
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.mmdev.meetapp.R


/* Created by A on 07.10.2019.*/

/**
 * This is the documentation block about the class
 */

class LoadingDialog(private var context: Context) {

	private lateinit var dialog: Dialog

	fun showDialog() {
		dialog = Dialog(context)
		dialog.requestWindowFeature(FEATURE_NO_TITLE)
		dialog.setCancelable(false)
		dialog.setContentView(R.layout.loading)

		val gifImageView: ImageView = dialog.findViewById(R.id.iv_loading)

		Glide.with(context)
			.load(R.drawable.loading)
			.placeholder(R.drawable.loading)
			.centerCrop()
			.apply(RequestOptions().circleCrop())
			.into(DrawableImageViewTarget(gifImageView))
		dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
		dialog.show()
	}

	fun dismissDialog() { if (dialog.isShowing) dialog.dismiss() }

}