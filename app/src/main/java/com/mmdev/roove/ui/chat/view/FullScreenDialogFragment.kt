/*
 * Created by Andrii Kovalchuk on 29.11.19 21:45
 * Copyright (c) 2019. All rights reserved.
 * Last modified 29.11.19 21:45
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.mmdev.roove.R


class FullScreenDialogFragment: DialogFragment() {

	private var isHide = false
	private var photoUrl = ""

	companion object {
		private const val PHOTO_KEY = "PHOTO_URL"
		fun newInstance(photoUrl: String) = DialogFragment().apply {
			arguments = Bundle().apply {
				putString(PHOTO_KEY, photoUrl)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		//setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
		arguments?.let {
			photoUrl = it.getString(PHOTO_KEY, "")
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View = inflater
		.inflate(R.layout.dialog_full_screen_image, container, false)


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val imageView = view.findViewById<ImageView>(R.id.fullscreen_imageView)
		imageView.setOnClickListener { fullScreenCall() }
		Log.wtf("mylogs", "dialog fragment recieved + $photoUrl")
		Glide.with(imageView.context)
			.load(photoUrl)
			.diskCacheStrategy(DiskCacheStrategy.RESOURCE)
			.into(imageView)
		// Set the content to appear under the system bars so that the
		// content doesn't resize when the system bars hide and show.
//		imageView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	}

	//hide bottom navigation to see fullscreen image
	private fun fullScreenCall() {
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
			// lower api
			val v = dialog?.window!!.decorView
			if (v.systemUiVisibility == View.VISIBLE) v.systemUiVisibility = View.GONE
			else v.systemUiVisibility = View.VISIBLE
		}
		else if (Build.VERSION.SDK_INT >= 19) {
			//for new api versions.
			val decorView = dialog?.window!!.decorView
			if (!isHide) {
				decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
				isHide = true
			}
			else {
				decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
				isHide = false
			}
		}
	}


}
