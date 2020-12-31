/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mmdev.roove.R
import com.mmdev.roove.databinding.DialogChatFullScreenImageBinding

/**
 * //todo: replace deprecated calls
 */

class FullScreenDialogFragment: DialogFragment() {
	
	private var _binding: DialogChatFullScreenImageBinding? = null
	private val binding: DialogChatFullScreenImageBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private var isHide = false
	private var receivedPhotoUrl = ""

	companion object {
		private const val PHOTO_KEY = "PHOTO_URL"
		fun newInstance(photoUrl: String) = FullScreenDialogFragment().apply {
			arguments = Bundle().apply {
				putString(PHOTO_KEY, photoUrl)
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
		arguments?.let {
			receivedPhotoUrl = it.getString(PHOTO_KEY, "")
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = DialogChatFullScreenImageBinding.inflate(inflater, container, false)
		.apply {
			this.photoUrl = receivedPhotoUrl
			_binding = this
			executePendingBindings()
		}
		.root

	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		// Set the content to appear under the system bars so that the
		// content doesn't resize when the system bars hide and show.
		ivDialogFullScreen.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
				View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

		ivDialogFullScreen.setOnClickListener { fullScreenCall() }
	}

	//hide bottom navigation to see fullscreen image
	private fun fullScreenCall() {
		//for new api versions >= 19
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
