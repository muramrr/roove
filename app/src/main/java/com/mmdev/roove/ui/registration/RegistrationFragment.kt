/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

package com.mmdev.roove.ui.registration

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import com.mmdev.domain.photo.PhotoItem
import com.mmdev.domain.user.UserState
import com.mmdev.domain.user.data.BaseUserInfo
import com.mmdev.domain.user.data.Gender
import com.mmdev.domain.user.data.SelectionPreferences
import com.mmdev.domain.user.data.SelectionPreferences.PreferredAgeRange
import com.mmdev.domain.user.data.SelectionPreferences.PreferredGender
import com.mmdev.domain.user.data.SelectionPreferences.PreferredGender.EVERYONE
import com.mmdev.domain.user.data.SelectionPreferences.PreferredGender.FEMALE
import com.mmdev.domain.user.data.SelectionPreferences.PreferredGender.MALE
import com.mmdev.domain.user.data.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentRegistrationBinding
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.utils.extensions.observeOnce
import dagger.hilt.android.AndroidEntryPoint

/**
 * This is the documentation block about the class
 */

@AndroidEntryPoint
class RegistrationFragment: BaseFragment<AuthViewModel, FragmentRegistrationBinding>(
	layoutId = R.layout.fragment_registration
){
	
	override val mViewModel: AuthViewModel by viewModels()
	
	private var registrationStep = 1

	private var baseUserInfo = BaseUserInfo()
	private var name = "no name"
	private var age = 18
	private var gender: Gender? = null
	private var preferredGender: PreferredGender? = null
	private var preferredAgeMin = 18f
	private var preferredAgeMax = 24f
	
	private var preferredGenderToDisplay = ""
	private var genderToDisplay = ""
	

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		sharedViewModel.userInfoForRegistration.observeOnce(this, {
			baseUserInfo = it.baseUserInfo
		})
		
		mViewModel.signUpDone.observeOnce(this, {
			sharedViewModel.userState.postValue(UserState.registered(it))
		})
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {

		containerRegistration.transitionToState(R.id.step_1)

		disableFab()

		// prevent to click buttons during transitions
		containerRegistration.setTransitionListener(object : MotionLayout.TransitionListener {

			override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

			override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
				btnRegistrationBack.isClickable = false
				btnRegistrationNext.isClickable = false
			}

			override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

			override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
				btnRegistrationBack.isClickable = true
				btnRegistrationNext.isClickable = true
			}
		})



		//step 1 your gender
		btnGenderMale.setOnClickListener {
			setSelectedMale()

			when (registrationStep) {
				1 -> {
					gender = Gender.MALE
					genderToDisplay = getString(R.string.genderMale)
				}
				2 -> {
					preferredGender = MALE
					preferredGenderToDisplay = getString(R.string.preferredGenderMale)
				}
			}

			enableFab()

		}

		btnGenderFemale.setOnClickListener {
			setSelectedFemale()

			when (registrationStep){
				1 -> {
					gender = Gender.FEMALE
					genderToDisplay = getString(R.string.genderFemale)
				}
				2 -> {
					preferredGender = FEMALE
					preferredGenderToDisplay = getString(R.string.preferredGenderFemale)
				}
			}

			enableFab()

		}


		//step 2 gender your prefer
		btnGenderEveryone.setOnClickListener {
			setSelectedEveryone()
			preferredGender = EVERYONE
			preferredGenderToDisplay = getString(R.string.preferredGenderEveryone)
			enableFab()
		}


		//step 3 age
		sliderAge.addOnChangeListener { _, value, _ ->
			age = value.toInt()
			tvAgeDisplay.text = age.toString()
		}
		//step 3 pref age
		rangeSeekBarRegAgePicker.addOnChangeListener { rangeSlider, value, fromUser ->
			preferredAgeMin = rangeSlider.values.first()
			tvPickedAgeMin.text = "${preferredAgeMin.toInt()}"
			
			preferredAgeMax = rangeSlider.values.last()
			tvPickedAgeMax.text = "${preferredAgeMax.toInt()}"
		}
		
		//step 4 name
		edInputChangeName.doAfterTextChanged {
			if (it.isNullOrBlank()) {
				layoutInputChangeName.error = getString(R.string.text_empty_error)
				disableFab()
			}
			else {
				enableFab()
				layoutInputChangeName.error = ""
				name = it.toString().trim()
			}
		}
		

		//final step
		btnFinalBack.setOnClickListener {
			containerRegistration.transitionToState(R.id.step_4)
			restoreStep4State()
			registrationStep -= 1
		}

		btnRegistrationDone.setOnClickListener {
			val finalUserModel = baseUserInfo.copy(
				name = name,
				age = age,
				gender = gender!!
			)

			mViewModel.signUp(
				UserItem(
					finalUserModel,
					photoURLs = listOf(PhotoItem.FACEBOOK_PHOTO(finalUserModel.mainPhotoUrl)),
					preferences = SelectionPreferences(
						gender = preferredGender ?: EVERYONE,
						ageRange = PreferredAgeRange(
							minAge = preferredAgeMin.toInt(),
							maxAge = preferredAgeMax.toInt()
						)
					)
				)
			)
		}



		btnRegistrationBack.setOnClickListener {
			when (registrationStep) {
				1 -> navController.navigateUp()

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
					containerRegistration.transitionToState(R.id.step_final)
					setupFinalForm()
				}
			}
			registrationStep += 1

			//Toast.makeText(context, "reg step = $registrationStep", Toast.LENGTH_SHORT).show()
		}

	}

	private fun restoreStep1State() {
		setUnselectedAllButtons()
		changeStringsForSelfChoosing()
		if (gender != null){
			enableFab()
			when (gender) {
				Gender.MALE -> setSelectedMale()
				Gender.FEMALE -> setSelectedFemale()
			}
		}
		else disableFab()
	}

	private fun restoreStep2State() {
		setUnselectedAllButtons()
		changeStringsForPreferred()
		if (preferredGender != null){
			enableFab()
			when (preferredGender){
				MALE -> setSelectedMale()
				FEMALE -> setSelectedFemale()
				EVERYONE -> setSelectedEveryone()
			}
		}
		else disableFab()
	}

	private fun restoreStep3State() = binding.run {
		enableFab()
		sliderAge.value = age.toFloat()
		tvAgeDisplay.text = age.toString()
		rangeSeekBarRegAgePicker.setValues(preferredAgeMin, preferredAgeMax)
	}

	private fun restoreStep4State() = binding.run {
		disableFab()
		if (name.isNotEmpty() && name != "no name") {
			edInputChangeName.setText(name)
		}
		else if (baseUserInfo.name.isNotEmpty()) {
			edInputChangeName.setText(baseUserInfo.name)
		}
		else if (edInputChangeName.text.isNullOrEmpty()) {
			layoutInputChangeName.error = getString(R.string.text_empty_error)
		}
		else enableFab()

	}

	private fun setupFinalForm() = binding.run {
		edFinalName.setText(name)
		edFinalGender.setText(genderToDisplay)
		edFinalPreferredGender.setText(preferredGenderToDisplay)
		edFinalAge.setText(age.toString())
	}


	private fun enableFab() {
		if (!binding.btnRegistrationNext.isEnabled) binding.btnRegistrationNext.isEnabled = true
	}

	private fun disableFab() {
		if (binding.btnRegistrationNext.isEnabled) binding.btnRegistrationNext.isEnabled = false
	}

	private fun changeStringsForSelfChoosing() {
		binding.tvGenderFemale.text = getString(R.string.genderFemale)
		binding.tvGenderMale.text = getString(R.string.genderMale)
	}

	private fun changeStringsForPreferred() {
		binding.tvGenderFemale.text = getString(R.string.preferredGenderFemale)
		binding.tvGenderMale.text = getString(R.string.preferredGenderMale)
	}

	private fun setUnselectedAllButtons() = binding.run {
		btnGenderEveryone.isSelected = false
		btnGenderFemale.isSelected = false
		btnGenderMale.isSelected = false
	}

	private fun setSelectedMale() = binding.run {
		btnGenderEveryone.isSelected = false
		btnGenderFemale.isSelected = false
		btnGenderMale.isSelected = true
	}

	private fun setSelectedFemale() = binding.run {
		btnGenderEveryone.isSelected = false
		btnGenderFemale.isSelected = true
		btnGenderMale.isSelected = false

	}

	private fun setSelectedEveryone() = binding.run {
		btnGenderEveryone.isSelected = true
		btnGenderFemale.isSelected = false
		btnGenderMale.isSelected = false
	}

}