/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 21.12.19 20:14
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.ArrayAdapter
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mmdev.business.base.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.utils.addSystemBottomPadding
import com.mmdev.roove.utils.addSystemTopPadding
import kotlinx.android.synthetic.main.fragment_registration.*


/**
 * This is the documentation block about the class
 */

class RegistrationFragment: BaseFragment(R.layout.fragment_registration){

	private lateinit var authViewModel: AuthViewModel

	private var registrationStep = 1
	private var isRegistrationCompleted = false

	private var baseUserInfo = BaseUserInfo()
	private var name = "no name"
	private var age = 0
	private var city = ""
	private var gender = ""
	private var preferredGender = ""

	private var cityToDisplay = ""


	private lateinit var cityList: Map<String, String>


	private var pressedTintColor: ColorStateList? = null
	private var unpressedTintColor: ColorStateList? = null

	companion object{
		private const val TAG_LOG = "mylogs"
	}


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

		authViewModel = activity?.run {
			ViewModelProvider(this, factory)[AuthViewModel::class.java]
		} ?: throw Exception("Invalid Activity")
		authViewModel.getBaseUserInfo().observe(this, Observer {
			baseUserInfo = it
		})


	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		containerRegistration.addSystemBottomPadding()
		tvAllCorrect.addSystemTopPadding()
		tvInterested.addSystemTopPadding()

		containerRegistration.transitionToState(R.id.step_1)

		disableFab()

		pressedTintColor = ContextCompat.getColorStateList(context!!, R.color.lolite)
		unpressedTintColor = ContextCompat.getColorStateList(context!!, R.color.colorPrimary)

		// don't allow to break transitions
		containerRegistration.setTransitionListener(
				object : MotionLayout.TransitionListener {

					override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean,
					                                 p3: Float) {}

					override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
						btnRegistrationBack.isClickable = false
						btnRegistrationNext.isClickable = false
					}

					override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

					override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
						btnRegistrationBack.isClickable = true
						btnRegistrationNext.isClickable = true
					}
				}
		)



		//step 1
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


		//step 2
		btnGenderEveryone.setOnClickListener {
			setPressedEveryone()
			preferredGender = btnGenderEveryone.text.toString()
			enableFab()
		}


		//step 3
		sliderAge.setOnChangeListener{ _, value ->
			age = value.toInt()
			tvAgeDisplay.text = age.toString()
			enableFab()
		}


		//step 4
		edInputChangeName.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				layoutInputChangeName.isCounterEnabled = true
			}

			override fun afterTextChanged(s: Editable) {
				when {
					s.length > layoutInputChangeName.counterMaxLength -> {
						layoutInputChangeName.error = "Max length reached"
						disableFab()
					}
					s.isEmpty() -> {
						layoutInputChangeName.error = "Name must not be empty"
						disableFab()
					}
					else -> {
						enableFab()
						layoutInputChangeName.error = ""
						name = s.toString().trim()
					}
				}
			}
		})

		edInputChangeName.setOnEditorActionListener { v, actionId, _ ->
			if (actionId == IME_ACTION_DONE) {
				v.text = v.text.toString().trim()
				v.clearFocus()
			}
			return@setOnEditorActionListener false
		}


		//step 5
		val cityAdapter = ArrayAdapter<String>(context!!,
		                                       R.layout.fragment_reg_drop_item,
		                                       cityList.map { it.key })
		dropdownCityChooser.setAdapter(cityAdapter)

		dropdownCityChooser.setOnItemClickListener { _, _, position, _ ->
			city = cityList.map { it.value }[position]
			cityToDisplay = cityList.map { it.key }[position]
			enableFab()
		}


		//final step
		btnFinalBack.setOnClickListener {
			containerRegistration.transitionToState(R.id.step_5)
			restoreStep5State()
			registrationStep -= 1
		}

		btnRegistrationDone.setOnClickListener {
			val finalUserModel = BaseUserInfo(name, age, city, gender,
			                                  baseUserInfo.mainPhotoUrl,
			                                  baseUserInfo.userId)

			authViewModel.signUp(UserItem(finalUserModel,
			                              preferredGender,
			                              listOf(finalUserModel.mainPhotoUrl)))
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

				4 -> {
					containerRegistration.transitionToState(R.id.step_3)
					restoreStep3State()
				}

				5 -> {
					containerRegistration.transitionToState(R.id.step_4)
					restoreStep4State()
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
					containerRegistration.transitionToState(R.id.step_3)
					restoreStep3State()
				}

				3 -> {
					containerRegistration.transitionToState(R.id.step_4)
					restoreStep4State()
				}

				4 -> {
					containerRegistration.transitionToState(R.id.step_5)
					restoreStep5State()
				}

				5 -> {
					containerRegistration.transitionToState(R.id.step_final)
					setupFinalForm()
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

	private fun restoreStep3State(){
		if (age != 0) {
			enableFab()
			sliderAge.value = age.toFloat()
			tvAgeDisplay.text = age.toString()
		}
		else disableFab()
	}

	private fun restoreStep4State(){
		if (name.isNotEmpty() && name != "no name") {
			edInputChangeName.setText(name)
		}
		else if (baseUserInfo.name.isNotEmpty()) {
			edInputChangeName.setText(baseUserInfo.name)
		}
		else if (edInputChangeName.text.isNullOrEmpty()) {
			layoutInputChangeName.error = "Name must not be empty"
			disableFab()
		}
		layoutInputChangeName.isCounterEnabled = false
	}

	private fun restoreStep5State(){
		if (city.isNotEmpty()) {
			enableFab()
		}
		else disableFab()
	}

	private fun setupFinalForm(){
		edFinalName.setText(name)
		edFinalGender.setText(gender)
		edFinalPreferredGender.setText(preferredGender)
		edFinalAge.setText(age.toString())
		edFinalCity.setText(cityToDisplay)
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

	override fun onStop(){
		if (!isRegistrationCompleted) {
			authViewModel.logOut()
		}
		super.onStop()
	}

}