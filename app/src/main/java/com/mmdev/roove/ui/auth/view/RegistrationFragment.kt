/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 11.12.19 22:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

	private var registrationStep = 1

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

		disableFab()

		var gender = ""
		var preferredGender = ""

		val pressedTintColor = ContextCompat.getColorStateList(context!!,
		                                                       R.color.lolite)

		val unpressedTintColor = ContextCompat.getColorStateList(context!!,
		                                                         R.color.colorPrimary)


		btnGenderMale.setOnClickListener {

			when(registrationStep){
				1 -> gender = btnGenderMale.text.toString()
				2 -> preferredGender = btnGenderMale.text.toString()
			}

			enableFab()

			btnGenderMale.backgroundTintList = pressedTintColor
			btnGenderFemale.backgroundTintList = unpressedTintColor
			btnGenderEveryone.backgroundTintList = unpressedTintColor

		}

		btnGenderFemale.setOnClickListener {

			when(registrationStep){
				1 -> gender = btnGenderFemale.text.toString()
				2 -> preferredGender = btnGenderFemale.text.toString()
			}

			enableFab()

			btnGenderFemale.backgroundTintList = pressedTintColor
			btnGenderMale.backgroundTintList = unpressedTintColor
			btnGenderEveryone.backgroundTintList = unpressedTintColor

		}


		btnGenderEveryone.setOnClickListener {

			preferredGender = btnGenderEveryone.text.toString()
			enableFab()

			btnGenderEveryone.backgroundTintList = pressedTintColor
			btnGenderFemale.backgroundTintList = unpressedTintColor
			btnGenderMale.backgroundTintList = unpressedTintColor


		}

		btnRegistrationBack.setOnClickListener {
			if (registrationStep == 1) findNavController().navigateUp()
			else {
				btnRegistrationBack.isEnabled = registrationStep > 1
				registrationStep -= 1
				//authViewModel.signUp(userItemModel)
				isRegistrationCompleted = true
				containerRegistration.transitionToStart()
				enableFab()
			}
			Toast.makeText(context,
			               "registration step = $registrationStep",
			               Toast.LENGTH_SHORT).show()

		}

		btnRegistrationNext.setOnClickListener {
			//authViewModel.signUp(userItemModel)
			registrationStep += 1
			isRegistrationCompleted = true
			containerRegistration.transitionToEnd()
			disableFab()
			Toast.makeText(context,
			               "registration step = $registrationStep",
			               Toast.LENGTH_SHORT).show()
		}


	}


	private fun enableFab(){
		if (!btnRegistrationNext.isEnabled) {
			val pressedFabTintColor =
				ContextCompat.getColorStateList(context!!, R.color.gradient3)
			btnRegistrationNext.isEnabled = true
			btnRegistrationNext.backgroundTintList = pressedFabTintColor

		}
	}

	private fun disableFab(){
		if (btnRegistrationNext.isEnabled) {
			val unpressedFabTintColor =
				ContextCompat.getColorStateList(context!!, R.color.disabled_color)
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