/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 14.01.20 19:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import kotlinx.android.synthetic.main.fragment_settings_account.*


/**
 * This is the documentation block about the class
 */

class SettingsAccountFragment: BaseFragment(R.layout.fragment_settings_account) {

	private lateinit var userItemModel: UserItem

	private lateinit var cityList: Map<String, String>

	private lateinit var sharedViewModel: SharedViewModel

	override fun onAttach(context: Context) {
		super.onAttach(context)
		cityList = mapOf(context.getString(R.string.russia_ekb) to "ekb",
		                 context.getString(R.string.russia_krasnoyarsk) to "krasnoyarsk",
		                 context.getString(R.string.russia_krd) to "krd",
		                 context.getString(R.string.russia_kzn) to "kzn",
		                 context.getString(R.string.russia_msk) to "msk",
		                 context.getString(R.string.russia_nnv) to "nnv",
		                 context.getString(R.string.russia_nsk) to "nsk",
		                 context.getString(R.string.russia_sochi) to "sochi",
		                 context.getString(R.string.russia_spb) to "spb")
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		activity?.run {
			sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		userItemModel = sharedViewModel.currentUser.value!!
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		edInputChangeName.setText(userItemModel.baseUserInfo.name)

		val cityAdapter = ArrayAdapter<String>(context!!,
		                                       R.layout.fragment_reg_drop_item,
		                                       cityList.map { it.key })
		dropdownCityChooser.setAdapter(cityAdapter)

		dropdownCityChooser.setText(userItemModel.baseUserInfo.city)
	}
}