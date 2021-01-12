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

package com.mmdev.roove.ui.settings.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.domain.user.data.SelectionPreferences.PreferredGender
import com.mmdev.domain.user.data.SelectionPreferences.PreferredGender.*
import com.mmdev.domain.user.data.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.BtmSheetSettingsPreferencesBinding
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.SharedViewModel

class SettingsPreferencesBottomSheet : BottomSheetDialogFragment() {
	
	private var _binding: BtmSheetSettingsPreferencesBinding? = null
	private val binding: BtmSheetSettingsPreferencesBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private val sharedViewModel: SharedViewModel by activityViewModels()
	
	
	private var newPreferredGender: PreferredGender? = null
	private var newPreferredMinAge: Float? = null
	private var newPreferredMaxAge: Float? = null
	private var newRadius: Float? = null
	
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = BtmSheetSettingsPreferencesBinding.inflate(inflater, container, false)
		.apply {
			_binding = this
			executePendingBindings()
		}
		.root


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		initProfile(MainActivity.currentUser!!)
		
		rangeSeekBarAgePicker.addOnChangeListener { rangeSlider, value, fromUser ->
			newPreferredMinAge = rangeSlider.values.first()
			newPreferredMaxAge = rangeSlider.values.last()
			
			tvPickedAgeMin.text = "${rangeSlider.values.first().toInt()}"
			tvPickedAgeMax.text = "${rangeSlider.values.last().toInt()}"
		}

		
		toggleButtonPickerPreferredGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
			if (group.checkedButtonIds.size > 1)
				newPreferredGender = EVERYONE
			
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderMale)
				newPreferredGender = MALE
			
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderFemale)
				newPreferredGender = FEMALE
		}
		
		sliderRadius.addOnChangeListener { slider, value, fromUser ->
			newRadius = value
		}
	}

	private fun initProfile(userItem: UserItem) = binding.run {
		rangeSeekBarAgePicker.setValues(
			userItem.preferences.ageRange.minAge.toFloat(),
			userItem.preferences.ageRange.maxAge.toFloat()
		)
		tvPickedAgeMin.text = "${userItem.preferences.ageRange.minAge}"
		tvPickedAgeMax.text = "${userItem.preferences.ageRange.maxAge}"
		
		when (userItem.preferences.gender) {
			MALE -> {
				toggleButtonPickerPreferredGender.clearChecked()
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderMale)
			}
			FEMALE -> {
				toggleButtonPickerPreferredGender.clearChecked()
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderFemale)
			}
			EVERYONE -> {
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderMale)
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderFemale)
			}
		}
		
		sliderRadius.value = userItem.preferences.radius.toFloat()
		
	}
	
	override fun onStop() {
		if (newPreferredGender != null || newPreferredMaxAge != null || newPreferredMinAge != null || newRadius != null)
			with(MainActivity.currentUser!!.preferences) {
				sharedViewModel.updateUser(
					MainActivity.currentUser!!.copy(
						preferences = copy(
							gender = newPreferredGender ?: gender,
							ageRange = ageRange.copy(
								minAge = newPreferredMinAge?.toInt() ?: ageRange.minAge,
								maxAge = newPreferredMaxAge?.toInt() ?: ageRange.maxAge
							),
							radius = (newRadius ?: radius).toDouble()
						)
					)
				)
			}
			
		super.onStop()
	}
}