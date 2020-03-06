/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.03.20 17:22
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mmdev.business.core.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.core.injector
import com.mmdev.roove.ui.core.viewmodel.SharedViewModel
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_settings_modal_bottom_sheet.*

class SettingsModalBottomSheet : BottomSheetDialogFragment() {

	private var dismissWithAnimation = false
	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var userItem: UserItem

	private lateinit var male: String
	private lateinit var female: String
	private lateinit var everyone: String


	companion object {
		private const val ARG_DISMISS_WITH_ANIMATION = "dismiss_with_animation"
		fun newInstance(dismissWithAnimation: Boolean): SettingsModalBottomSheet {
			val modalBottomSheet = SettingsModalBottomSheet()
			modalBottomSheet.arguments = bundleOf(ARG_DISMISS_WITH_ANIMATION to dismissWithAnimation)
			return modalBottomSheet
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		male = getString(R.string.genderMale)
		female = getString(R.string.genderFemale)
		everyone = getString(R.string.genderEveryone)
		activity?.run {
			sharedViewModel = ViewModelProvider(this, injector.factory())[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")
		sharedViewModel.getCurrentUser().observeOnce(this, Observer {
			userItem = it
			initProfile(it)
		})
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
		inflater.inflate(R.layout.fragment_settings_modal_bottom_sheet, container, false)


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		rangeSeekBarAgePicker.setOnRangeSeekBarChangeListener { _, number, number2 ->
			userItem.preferredAgeRange.minAge = number.toInt()
			userItem.preferredAgeRange.maxAge = number2.toInt()
		}

//		btnPickerPreferredGenderMale.addOnCheckedChangeListener { button, isChecked ->
//			if (toggleButtonPickerPreferredGender.checkedButtonIds.size == 1 && isChecked)
//				userItem.baseUserInfo.preferredGender = male
//		}
//
//		btnPickerPreferredGenderFemale.addOnCheckedChangeListener { button, isChecked ->
//			if (toggleButtonPickerPreferredGender.checkedButtonIds.size == 1 && isChecked)
//				userItem.baseUserInfo.preferredGender = female
//		}


		toggleButtonPickerPreferredGender.addOnButtonCheckedListener { group, checkedId, isChecked ->
			if (group.checkedButtonIds.size > 1)
				userItem.baseUserInfo.preferredGender = everyone
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderMale)
				userItem.baseUserInfo.preferredGender = male
			if (group.checkedButtonIds.size == 1 && group.checkedButtonIds[0] == R.id.btnPickerPreferredGenderFemale)
				userItem.baseUserInfo.preferredGender = female

			Log.wtf("mylogs", userItem.baseUserInfo.preferredGender)
			Log.wtf("mylogs", "${group.checkedButtonIds}")
		}

	}

	private fun initProfile(userItem: UserItem) {
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

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		dismissWithAnimation = arguments?.getBoolean(ARG_DISMISS_WITH_ANIMATION) ?: false
		(requireDialog() as BottomSheetDialog).dismissWithAnimation = dismissWithAnimation
	}
}