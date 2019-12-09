/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 09.12.19 21:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.mmdev.business.user.entity.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.BaseFragment
import kotlinx.android.synthetic.main.fragment_auth_registration.*


/**
 * This is the documentation block about the class
 */

class RegistrationFragment: BaseFragment(R.layout.fragment_auth_registration){

	private var isRegistrationCompleted = false

	private lateinit var authViewModel: AuthViewModel

	private lateinit var userItem: UserItem

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		authViewModel = activity?.run {
			ViewModelProvider(this, factory)[AuthViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		var gender = "male"
		var preferredGender = "male"

		tlRegistrationYourGender.addOnTabSelectedListener(object: OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				gender = tab.text.toString()
				Toast.makeText(context, gender, Toast.LENGTH_SHORT).show()
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})

		tlRegistrationPreferredGender.addOnTabSelectedListener(object: OnTabSelectedListener {
			override fun onTabSelected(tab: TabLayout.Tab) {
				preferredGender = tab.text.toString()
				Toast.makeText(context, preferredGender, Toast.LENGTH_SHORT).show()
			}

			override fun onTabUnselected(tab: TabLayout.Tab) {}
			override fun onTabReselected(tab: TabLayout.Tab) {}
		})


		btnRegistrationDone.setOnClickListener {
			//authViewModel.signUp(userItemModel)
			isRegistrationCompleted = true

		}
	}

	override fun onStop() {
		if (!isRegistrationCompleted) {
			authViewModel.logOut()
		}
		super.onStop()
	}

}