/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 13.12.19 19:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
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

	private var gender = ""
	private var preferredGender = ""

	private var pressedTintColor: ColorStateList? = null
	private var unpressedTintColor: ColorStateList? = null

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

		pressedTintColor = ContextCompat.getColorStateList(context!!, R.color.lolite)
		unpressedTintColor = ContextCompat.getColorStateList(context!!, R.color.colorPrimary)


		btnGenderMale.setOnClickListener {
			setPressedMale()

			when (registrationStep){
				1 -> gender = btnGenderMale.text.toString()
				2 -> preferredGender = btnGenderMale.text.toString()
			}

			enableFab()

		}

		btnGenderFemale.setOnClickListener {
			setPressedFemale()

			when (registrationStep){
				1 -> gender = btnGenderFemale.text.toString()
				2 -> preferredGender = btnGenderFemale.text.toString()
			}

			enableFab()

		}

		btnGenderEveryone.setOnClickListener {
			setPressedEveryone()

			preferredGender = btnGenderEveryone.text.toString()
			enableFab()

		}

		btnRegistrationBack.setOnClickListener {
			when (registrationStep) {
				1 -> findNavController().navigateUp()

				2 -> {
					containerRegistration.transitionToState(R.id.step_1)
					restoreStep1State()
				}
				3 -> {
					containerRegistration.transitionToState(R.id.step_2)
					restoreStep2State()

				}
			}
			registrationStep -= 1

			//Toast.makeText(context, "reg step = $registrationStep", Toast.LENGTH_SHORT).show()
		}

		btnRegistrationNext.setOnClickListener {
			when (registrationStep) {
				1 -> {
					containerRegistration.transitionToState(R.id.step_2)
					restoreStep2State()
				}

				2 -> {

				}

				3 -> {

				}
			}
			registrationStep += 1


			//Toast.makeText(context, "reg step = $registrationStep", Toast.LENGTH_SHORT).show()
		}


	}

	private fun restoreStep1State(){
		clearAllButtonsState()
		if (gender.isNotEmpty()){
			enableFab()
			when (gender){
				"male" -> {
					setPressedMale()
				}
				"female" -> {
					setPressedFemale()
				}
			}
		}
		else disableFab()
	}

	private fun restoreStep2State(){
		clearAllButtonsState()
		if (preferredGender.isNotEmpty()){
			enableFab()
			when (preferredGender){
				"male" -> {
					setPressedMale()
				}
				"female" -> {
					setPressedFemale()
				}
				"everyone" ->{
					setPressedEveryone()
				}
			}
		}
		else disableFab()
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



	private fun clearAllButtonsState(){
		btnGenderMale.backgroundTintList = unpressedTintColor
		btnGenderFemale.backgroundTintList = unpressedTintColor
		btnGenderEveryone.backgroundTintList = unpressedTintColor
	}

	private fun setPressedMale(){
		btnGenderMale.backgroundTintList = pressedTintColor
		btnGenderFemale.backgroundTintList = unpressedTintColor
		btnGenderEveryone.backgroundTintList = unpressedTintColor
	}

	private fun setPressedFemale(){
		btnGenderMale.backgroundTintList = unpressedTintColor
		btnGenderFemale.backgroundTintList = pressedTintColor
		btnGenderEveryone.backgroundTintList = unpressedTintColor
	}

	private fun setPressedEveryone(){
		btnGenderEveryone.backgroundTintList = pressedTintColor
		btnGenderFemale.backgroundTintList = unpressedTintColor
		btnGenderMale.backgroundTintList = unpressedTintColor
	}

	override fun onStop() {
		if (!isRegistrationCompleted) {
			authViewModel.logOut()
		}
		super.onStop()
	}

}