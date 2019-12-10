/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 10.12.19 22:06
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mmdev.business.user.entity.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.utils.addSystemBottomPadding
import com.mmdev.roove.utils.addSystemTopPadding
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

		tv_reg_1.addSystemTopPadding()
		tv_reg_2.addSystemTopPadding()

		containerRegistration.addSystemBottomPadding()

		var gender = ""
		var preferredGender = ""

		val pressedTintColor = ContextCompat.getColorStateList(context!!,
		                                                       R.color.lolite)

		val unpressedTintColor = ContextCompat.getColorStateList(context!!,
		                                                         R.color.colorPrimary)

		val pressedFabTintColor = ContextCompat.getColorStateList(context!!,
		                                                       R.color.gradient3)

		val unpressedFabTintColor = ContextCompat.getColorStateList(context!!,
		                                                         R.color.black_50)

		btnGenderMale.setOnClickListener {
			gender = btnGenderMale.text.toString()
			btnRegistrationNext.backgroundTintList = pressedFabTintColor
			btnRegistrationNext.isEnabled = true

			btnGenderMale.backgroundTintList = pressedTintColor
			btnGenderFemale.backgroundTintList = unpressedTintColor
		}

		btnGenderFemale.setOnClickListener {
			gender = btnGenderFemale.text.toString()
			btnRegistrationNext.backgroundTintList = pressedFabTintColor
			btnRegistrationNext.isEnabled = true

			btnGenderFemale.backgroundTintList = pressedTintColor
			btnGenderMale.backgroundTintList = unpressedTintColor
		}

		btnPrefGenderMale.setOnClickListener {
			preferredGender = btnPrefGenderMale.text.toString()
			btnRegistrationNext.backgroundTintList = pressedFabTintColor
			btnRegistrationNext.isEnabled = true

			btnPrefGenderMale.backgroundTintList = pressedTintColor
			btnPrefGenderFemale.backgroundTintList = unpressedTintColor
			btnPrefGenderEveryone.backgroundTintList = unpressedTintColor
		}

		btnPrefGenderFemale.setOnClickListener {
			preferredGender = btnPrefGenderFemale.text.toString()
			btnRegistrationNext.backgroundTintList = pressedFabTintColor
			btnRegistrationNext.isEnabled = true

			btnPrefGenderFemale.backgroundTintList = pressedTintColor
			btnPrefGenderMale.backgroundTintList = unpressedTintColor
			btnPrefGenderEveryone.backgroundTintList = unpressedTintColor
		}

		btnPrefGenderEveryone.setOnClickListener {
			preferredGender = btnPrefGenderEveryone.text.toString()
			btnRegistrationNext.backgroundTintList = pressedFabTintColor
			btnRegistrationNext.isEnabled = true

			btnPrefGenderEveryone.backgroundTintList = pressedTintColor
			btnPrefGenderFemale.backgroundTintList = unpressedTintColor
			btnPrefGenderMale.backgroundTintList = unpressedTintColor

		}

		btnRegistrationBack.setOnClickListener {
			//authViewModel.signUp(userItemModel)
			isRegistrationCompleted = true
			containerRegistration.transitionToStart()
			btnRegistrationNext.isEnabled = true
			btnRegistrationNext.backgroundTintList = pressedFabTintColor
		}

		btnRegistrationNext.setOnClickListener {
			//authViewModel.signUp(userItemModel)
			isRegistrationCompleted = true
			containerRegistration.transitionToEnd()
			btnRegistrationNext.isEnabled = false
			btnRegistrationNext.backgroundTintList = unpressedFabTintColor
		}


	}

	override fun onStop() {
		if (!isRegistrationCompleted) {
			authViewModel.logOut()
		}
		super.onStop()
	}

}