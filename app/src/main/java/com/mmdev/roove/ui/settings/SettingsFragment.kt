/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.02.20 17:42
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.custom.HorizontalCarouselLayoutManager
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * This is the documentation block about the class
 */

class SettingsFragment: BaseFragment(R.layout.fragment_settings) {

	private val mSettingsPhotoAdapter = SettingsUserPhotoAdapter(listOf())

	private lateinit var sharedViewModel: SharedViewModel

	private lateinit var userItem: UserItem

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		sharedViewModel.getCurrentUser().observeOnce(this, Observer {
			userItem = it
			mSettingsPhotoAdapter.updateData(it.photoURLs)
			Log.wtf("mylogs", "${it.photoURLs.size}")
		})
	}


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		rvUserPhotosList.apply {
			adapter = mSettingsPhotoAdapter
			layoutManager = HorizontalCarouselLayoutManager(context, HORIZONTAL, false)
		}
	}

}