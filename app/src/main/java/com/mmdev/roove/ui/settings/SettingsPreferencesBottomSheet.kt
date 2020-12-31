/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 17:12
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.databinding.BtmSheetSettingsPreferencesBinding
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.utils.extensions.observeOnce

class SettingsPreferencesBottomSheet : BottomSheetDialogFragment() {
	
	private var _binding: BtmSheetSettingsPreferencesBinding? = null
	private val binding: BtmSheetSettingsPreferencesBinding
		get() = _binding ?: throw IllegalStateException(
			"Trying to access the binding outside of the view lifecycle."
		)
	
	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var userItem: UserItem

	private val male = "male"
	private val female = "female"
	private val everyone = "everyone"
	private var isChanged: Boolean = false
	

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.run {
			sharedViewModel = ViewModelProvider(this, injector.factory())[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		sharedViewModel.getCurrentUser().observeOnce(this, {
			userItem = it
			initProfile(it)
		})
	}
	
	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View = BtmSheetSettingsPreferencesBinding.inflate(inflater, container, false)
		.apply {
			_binding = this
			executePendingBindings()
		}
		.root


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		rangeSeekBarAgePicker.setOnRangeSeekBarChangeListener { _, number, number2 ->
			userItem.preferredAgeRange.minAge = number.toInt()
			userItem.preferredAgeRange.maxAge = number2.toInt()
			isChanged = true
		}

		toggleButtonPickerPreferredGender.addOnButtonCheckedListener { group, _, _ ->
			if (group.checkedButtonIds.size > 1)
				userItem.baseUserInfo.preferredGender = everyone
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderMale)
				userItem.baseUserInfo.preferredGender = male
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderFemale)
				userItem.baseUserInfo.preferredGender = female
		}

		btnPickerPreferredGenderMale.setOnClickListener { isChanged = true }
		btnPickerPreferredGenderFemale.setOnClickListener { isChanged = true }
	}

	private fun initProfile(userItem: UserItem) = binding.run {
		when (userItem.baseUserInfo.preferredGender) {
			male -> {
				toggleButtonPickerPreferredGender.clearChecked()
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderMale)
			}
			female -> {
				toggleButtonPickerPreferredGender.clearChecked()
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderFemale)
			}
			everyone -> {
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderMale)
				toggleButtonPickerPreferredGender.check(R.id.btnPickerPreferredGenderFemale)
			}
		}
		rangeSeekBarAgePicker.selectedMinValue = userItem.preferredAgeRange.minAge
		rangeSeekBarAgePicker.selectedMaxValue = userItem.preferredAgeRange.maxAge
	}

	override fun onStop() {
		sharedViewModel.modalBottomSheetNeedUpdateExecution.value = isChanged
		super.onStop()
	}
}