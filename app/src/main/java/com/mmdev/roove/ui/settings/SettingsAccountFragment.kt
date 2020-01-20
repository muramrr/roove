/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.01.20 21:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.core.viewmodel.LocalUserRepoViewModel
import com.mmdev.roove.ui.core.viewmodel.RemoteUserRepoViewModel
import com.mmdev.roove.utils.addSystemBottomPadding
import com.mmdev.roove.utils.observeOnce
import kotlinx.android.synthetic.main.fragment_settings.*


/**
 * This is the documentation block about the class
 */

class SettingsAccountFragment: BaseFragment(R.layout.fragment_settings) {

	private lateinit var userItemModel: UserItem

	private var name = ""
	private var age = 0
	private var city = ""
	private var gender = ""
	private var preferredGender = ""

	private var cityToDisplay = ""

	private lateinit var cityList: Map<String, String>
	private lateinit var genderList: List<String>
	private lateinit var preferredGenderList: List<String>

	private lateinit var authViewModel: AuthViewModel
	private lateinit var localRepoViewModel: LocalUserRepoViewModel
	private lateinit var remoteRepoViewModel: RemoteUserRepoViewModel
	private lateinit var sharedViewModel: SharedViewModel

	companion object{
		private const val TAG = "mylogs_SettingsAccFragment"
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

		genderList = listOf("male", "female")
		preferredGenderList = listOf("male", "female", "everyone")

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		activity?.run {
			authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
			localRepoViewModel = ViewModelProvider(this, factory)[LocalUserRepoViewModel::class.java]
			remoteRepoViewModel= ViewModelProvider(this, factory)[RemoteUserRepoViewModel::class.java]
			sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		userItemModel = sharedViewModel.currentUser.value!!
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		containerSettingsAcc.addSystemBottomPadding()
		initProfile()

		changerNameSetup()
		changerGenderSetup()
		changerPreferredGenderSetup()
		changerAgeSetup()
		changerCitySetup()

		btnSettingsLogOut.setOnClickListener { showSignOutPrompt() }

		btnSettingsSave.setOnClickListener {
			remoteRepoViewModel.updateUserItem(userItemModel)
			remoteRepoViewModel.getUserUpdateStatus().observeOnce(this, Observer {
				if (it) localRepoViewModel.saveUserInfo(userItemModel)
			})
		}
	}


	private fun initProfile() {
		edSettingsName.setText(userItemModel.baseUserInfo.name)
		dropSettingsGender.setText(userItemModel.baseUserInfo.gender)
		dropSettingsPreferredGender.setText(userItemModel.preferredGender)
		tvSettingsAgeDisplay.text = "Age: ${userItemModel.baseUserInfo.age}"
		sliderSettingsAge.value = userItemModel.baseUserInfo.age.toFloat()

		cityToDisplay = cityList.filterValues { it == userItemModel.baseUserInfo.city }.keys.first()
		dropSettingsCity.setText(cityToDisplay)
	}

	private fun changerNameSetup() {
		edSettingsName.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				layoutSettingsChangeName.isCounterEnabled = true
			}

			override fun afterTextChanged(s: Editable) {
				when {
					s.length > layoutSettingsChangeName.counterMaxLength -> {
						layoutSettingsChangeName.error = "Max length reached"
					}
					s.isEmpty() -> {
						layoutSettingsChangeName.error = "Name must not be empty"

					}
					else -> {
						layoutSettingsChangeName.error = ""
						name = s.toString().trim()
						userItemModel.baseUserInfo.name = name
					}
				}
			}
		})

		edSettingsName.setOnEditorActionListener { v, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				v.text = v.text.toString().trim()
				v.clearFocus()
			}
			return@setOnEditorActionListener false
		}

	}

	private fun changerGenderSetup() {
		val genderAdapter = ArrayAdapter<String>(context!!,
		                                         R.layout.drop_text_item,
		                                         genderList)
		dropSettingsGender.setAdapter(genderAdapter)

		dropSettingsGender.setOnItemClickListener { _, _, position, _ ->
			gender = genderList[position]
			userItemModel.baseUserInfo.gender = gender
		}
	}

	private fun changerPreferredGenderSetup() {
		val preferredGenderAdapter = ArrayAdapter<String>(context!!,
		                                                  R.layout.drop_text_item,
		                                                  preferredGenderList)

		dropSettingsPreferredGender.setAdapter(preferredGenderAdapter )

		dropSettingsPreferredGender.setOnItemClickListener { _, _, position, _ ->
			preferredGender = preferredGenderList[position]
			userItemModel.preferredGender = preferredGender
		}
	}

	private fun changerCitySetup() {
		val cityAdapter = ArrayAdapter<String>(context!!,
		                                       R.layout.drop_text_item,
		                                       cityList.map { it.key })
		dropSettingsCity.setAdapter(cityAdapter)

		dropSettingsCity.setOnItemClickListener { _, _, position, _ ->
			city = cityList.map { it.value }[position]
			cityToDisplay = cityList.map { it.key }[position]
			userItemModel.baseUserInfo.city = city
		}
	}

	private fun changerAgeSetup() {
		sliderSettingsAge.setOnChangeListener{ _, value ->
			age = value.toInt()
			userItemModel.baseUserInfo.age = age
			tvSettingsAgeDisplay.text = "Age: $age"
		}
	}

	/*
	* log out pop up
	*/
	private fun showSignOutPrompt() {
		MaterialAlertDialogBuilder(context)
			.setTitle("Do you wish to log out?")
			.setMessage("This will permanently log you out.")
			.setPositiveButton("Log out") { dialog, _ ->
				authViewModel.logOut()
				dialog.dismiss()
			}
			.setNegativeButton("Cancel") { dialog, _ ->
				dialog.dismiss()
			}
			.show()

	}


}