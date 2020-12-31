/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
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

package com.mmdev.roove.ui.chat.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mmdev.roove.R
import com.mmdev.roove.databinding.DialogChatFullScreenImageBinding
import kotlinx.android.synthetic.main.dialog_chat_full_screen_image.*


class FullScreenDialogFragment: DialogFragment() {

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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		DialogChatFullScreenImageBinding.inflate(inflater, container, false)
			.apply { this.photoUrl = receivedPhotoUrl }
			.root


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
