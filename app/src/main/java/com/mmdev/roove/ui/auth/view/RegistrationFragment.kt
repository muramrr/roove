/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 18.12.19 18:08
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.auth.view

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.ArrayAdapter
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
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

	private lateinit var authViewModel: AuthViewModel

	private var registrationStep = 1
	private var isRegistrationCompleted = false

	private var userItem: UserItem? = UserItem(name = "Andrii")

	private var name = "no name"
	private var age = 0
	private var city = ""
	private var gender = ""
	private var preferredGender = ""

	private var cityToDisplay = ""

	private val cityList = mapOf("Екатеринбург" to "ekb",
	                             "Красноярск" to "krasnoyarsk",
	                             "Краснодар" to "krd",
	                             "Казань" to "kzn",
	                             "Москва" to "msk",
	                             "Нижний Новгород" to "nnv",
	                             "Новосибирск" to "nsk",
	                             "Сочи" to "sochi",
	                             "Санкт-Петербург" to "spb")

	private var pressedTintColor: ColorStateList? = null
	private var unpressedTintColor: ColorStateList? = null

	companion object{
		private const val TAG_LOG = "mylogs"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		authViewModel = activity?.run {
			ViewModelProvider(this, factory)[AuthViewModel::class.java]
		} ?: throw Exception("Invalid Activity")
		authViewModel.getUserItem().value?.let { userItem = it }


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
		sliderAge.setLabelFormatter(Slider.BasicLabelFormatter())

		sliderAge.setOnChangeListener{ _, value ->
			age = value.toInt()
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
		                                       R.layout.fragment_auth_drop_item,
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
			Log.wtf(TAG_LOG, "btn reg back clicked")
		}
		btnRegistrationDone.setOnClickListener {
			Log.wtf(TAG_LOG, "btn reg done clicked")
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
		}
		else disableFab()
	}

	private fun restoreStep4State(){
		if (name.isNotEmpty() && name != "no name") {
			edInputChangeName.setText(name)
		}
		else if (!userItem?.name.isNullOrEmpty()) {
			edInputChangeName.setText(userItem?.name)
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