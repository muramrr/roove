/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.cards.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.mmdev.roove.R
import com.mmdev.roove.databinding.DialogCardsMatchBinding

/**
 * This is the documentation block about the class
 */

class MatchDialogFragment: DialogFragment() {
	
	private var _binding: DialogCardsMatchBinding? = null
	private val binding: DialogCardsMatchBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private var receivedName = ""
	private var receivedPhotoUrl = ""
	
	companion object {

		private const val PHOTO_KEY = "PHOTO_URL"
		private const val NAME_KEY = "NAME"
		fun newInstance(name: String, photoUrl: String) = MatchDialogFragment().apply {
			arguments = Bundle().apply {
				putString(NAME_KEY, name)
				putString(PHOTO_KEY, photoUrl)
			}
		}
	}


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
		arguments?.let {
			receivedName = it.getString(NAME_KEY, "")
			receivedPhotoUrl = it.getString(PHOTO_KEY, "")
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	) = DialogCardsMatchBinding.inflate(inflater, container, false).apply {
		this.name = receivedName
		this.photoUrl = receivedPhotoUrl
		_binding = this
		executePendingBindings()
	}.root


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		binding.tvMatchDialogBack.setOnClickListener { dialog?.dismiss() }
	}
}