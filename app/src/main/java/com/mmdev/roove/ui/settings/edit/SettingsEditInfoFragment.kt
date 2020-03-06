/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 06.03.20 17:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.settings.edit

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mmdev.business.core.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.viewmodel.RemoteUserRepoViewModel
import com.mmdev.roove.ui.core.viewmodel.SharedViewModel
import com.mmdev.roove.ui.custom.GridItemDecoration
import com.mmdev.roove.utils.observeOnce
import com.mmdev.roove.utils.showToastText
import kotlinx.android.synthetic.main.fragment_settings_edit_info.*


/**
 * This is the documentation block about the class
 */

class SettingsEditInfoFragment: BaseFragment(R.layout.fragment_settings_edit_info) {

	private lateinit var userItem: UserItem

	private val mEditorPhotoAdapter = SettingsEditInfoPhotoAdapter(mutableListOf())

	private var name = ""
	private var age = 0
	private var city = ""
	private var gender = ""

	private var cityToDisplay = ""

	private var descriptionText = ""

	private lateinit var cityList: Map<String, String>

	private lateinit var remoteRepoViewModel: RemoteUserRepoViewModel
	private lateinit var sharedViewModel: SharedViewModel

	private lateinit var male: String
	private lateinit var female: String


	companion object{
		private const val TAG = "mylogs_SettingsEditFragment"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		male = getString(R.string.genderMale)
		female = getString(R.string.genderFemale)

		activity?.run {
			remoteRepoViewModel= ViewModelProvider(this, factory)[RemoteUserRepoViewModel::class.java]
			sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]

		} ?: throw Exception("Invalid Activity")

		sharedViewModel.getCurrentUser().observe(this, Observer {
			userItem = it
			initProfile(it)
		})

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		val context = view.context
		cityList = mapOf(context.getString(R.string.russia_ekb) to "ekb",
		                 context.getString(R.string.russia_krasnoyarsk) to "krasnoyarsk",
		                 context.getString(R.string.russia_krd) to "krd",
		                 context.getString(R.string.russia_kzn) to "kzn",
		                 context.getString(R.string.russia_msk) to "msk",
		                 context.getString(R.string.russia_nnv) to "nnv",
		                 context.getString(R.string.russia_nsk) to "nsk",
		                 context.getString(R.string.russia_sochi) to "sochi",
		                 context.getString(R.string.russia_spb) to "spb")

		rvSettingsEditPhotos.apply {
			layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
			adapter =  mEditorPhotoAdapter
			addItemDecoration(GridItemDecoration())
		}

		mEditorPhotoAdapter.setOnItemClickListener(object: SettingsEditInfoPhotoAdapter
		                                                   .OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {
				if (mEditorPhotoAdapter.itemCount > 1) {
					val photoToDelete = mEditorPhotoAdapter.getPhoto(position)

					val isMainPhotoDeleting = photoToDelete.fileUrl == userItem.baseUserInfo.mainPhotoUrl

					remoteRepoViewModel.deletePhoto(photoToDelete, userItem, isMainPhotoDeleting)

					remoteRepoViewModel.photoDeletionStatus.observeOnce(this@SettingsEditInfoFragment, Observer {
						if (it) {
							userItem.photoURLs.remove(photoToDelete)
                            mEditorPhotoAdapter.removeAt(position)
                            if (isMainPhotoDeleting) {
	                            userItem.baseUserInfo.mainPhotoUrl = userItem.photoURLs[0].fileUrl
	                           // Log.wtf(TAG, "изменилась главная фотка: ${userItem.baseUserInfo.mainPhotoUrl}")
                            }
							//else Log.wtf(TAG, "главная фотка без изменений")
                        }
                    })
				}
				else context.showToastText("Хотя бы 1 фотка должна быть")
			}
		})

		//touch event guarantee that if user want to scroll or touch outside of edit box
		//keyboard hide and edittext focus clear
		containerScrollSettings.setOnTouchListener { v, _ ->
			val iMM = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			iMM.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
			edSettingsEditDescription.clearFocus()
			return@setOnTouchListener false
		}

		btnSettingsEditGenderMale.setOnClickListener {
			gender = male
			userItem.baseUserInfo.gender = gender
			toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderMale)
		}

		btnSettingsEditGenderFemale.setOnClickListener {
			gender = female
			userItem.baseUserInfo.gender = gender
			toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderFemale)
		}


		btnSettingsEditSave.setOnClickListener {
			remoteRepoViewModel.updateUserItem(userItem)
			remoteRepoViewModel.getUserUpdateStatus().observeOnce(this, Observer {
				if (it) {
					sharedViewModel.userSelected.value = userItem
					context.showToastText("Successfully saved")
				}
			})
		}
	}


	private fun initProfile(userItem: UserItem) {
		mEditorPhotoAdapter.updateData(userItem.photoURLs)
		edSettingsEditName.setText(userItem.baseUserInfo.name)
		if (userItem.baseUserInfo.gender == male)
			toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderMale)
		else toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderFemale)
		tvSettingsEditAge.text = "Age: ${userItem.baseUserInfo.age}"
		sliderSettingsEditAge.value = userItem.baseUserInfo.age.toFloat()

		cityToDisplay = cityList.filterValues { it == userItem.baseUserInfo.city }.keys.first()
		dropSettingsEditCity.setText(userItem.cityToDisplay)

		edSettingsEditDescription.setText(userItem.aboutText)

		changerNameSetup()
		changerAgeSetup()
		changerCitySetup()
		changerDescriptionSetup()

	}

	private fun changerNameSetup() {
		edSettingsEditName.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				layoutSettingsEditName.isCounterEnabled = true
			}

			override fun afterTextChanged(s: Editable) {
				when {
					s.length > layoutSettingsEditName.counterMaxLength -> {
						layoutSettingsEditName.error = getString(R.string.text_max_length_error)
						btnSettingsEditSave.isEnabled = false
					}
					s.isEmpty() -> {
						layoutSettingsEditName.error = getString(R.string.text_empty_error)
						btnSettingsEditSave.isEnabled = false

					}
					else -> {
						layoutSettingsEditName.error = ""
						name = s.toString().trim()
						userItem.baseUserInfo.name = name
						btnSettingsEditSave.isEnabled = true
					}
				}
			}
		})

		edSettingsEditName.setOnEditorActionListener { editText, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				editText.text = editText.text.toString().trim()
				editText.clearFocus()
			}
			return@setOnEditorActionListener false
		}
	}

	private fun changerAgeSetup() {
		sliderSettingsEditAge.setOnChangeListener{ _, value ->
			age = value.toInt()
			userItem.baseUserInfo.age = age
			tvSettingsEditAge.text = "Age: $age"
		}
	}

	private fun changerCitySetup() {
		val cityAdapter = ArrayAdapter(context!!,
		                               R.layout.drop_text_item,
		                               cityList.map { it.key })
		dropSettingsEditCity.setAdapter(cityAdapter)

		dropSettingsEditCity.setOnItemClickListener { _, _, position, _ ->
			city = cityList.map { it.value }[position]
			cityToDisplay = cityList.map { it.key }[position]
			userItem.baseUserInfo.city = city
			userItem.cityToDisplay = cityToDisplay
		}
	}

	private fun changerDescriptionSetup() {

		edSettingsEditDescription.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				layoutSettingsEditDescription.isCounterEnabled = true
			}

			override fun afterTextChanged(s: Editable) {
				if (s.length > layoutSettingsEditDescription.counterMaxLength){
					layoutSettingsEditDescription.error = getString(R.string.text_max_length_error)
					btnSettingsEditSave.isEnabled = false
				}
				else {
					layoutSettingsEditDescription.error = ""
					descriptionText = s.toString().trim()
					userItem.aboutText = descriptionText
					btnSettingsEditSave.isEnabled = true
				}
			}
		})

		edSettingsEditDescription.setOnEditorActionListener { editText, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				editText.text = editText.text.toString().trim()
				editText.clearFocus()
			}
			return@setOnEditorActionListener false
		}

		edSettingsEditDescription.setOnTouchListener { view, event ->
			view.parent.requestDisallowInterceptTouchEvent(true)
			if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				view.parent.requestDisallowInterceptTouchEvent(false)
			}
			return@setOnTouchListener false
		}

	}

	override fun onResume() {
		super.onResume()
		if (this::userItem.isInitialized) initProfile(userItem)
	}

	override fun onBackPressed() {
		super.onBackPressed()
		findNavController().navigateUp()
	}

}