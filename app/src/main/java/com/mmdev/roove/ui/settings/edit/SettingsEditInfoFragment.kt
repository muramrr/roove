/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
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

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.user.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentSettingsEditInfoBinding
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.GridItemDecoration
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.ui.profile.RemoteRepoViewModel.DeletingStatus.IN_PROGRESS
import com.mmdev.roove.utils.extensions.observeOnce
import com.mmdev.roove.utils.extensions.showToastText

/**
 * This is the documentation block about the class
 */

class SettingsEditInfoFragment: BaseFragment<RemoteRepoViewModel, FragmentSettingsEditInfoBinding>(
	isViewModelActivityHosted = true
) {

	private lateinit var currentUser: UserItem
	private val mEditorPhotoAdapter = SettingsEditInfoPhotoAdapter(layoutId = R.layout.fragment_settings_edit_info_photo_item)

	private var name = ""
	private var age = 0
	private var gender = ""

	private var descriptionText = ""

	private val male = "male"
	private val female = "female"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mViewModel = getViewModel()
		sharedViewModel.getCurrentUser().observeOnce(this, {
			currentUser = it
			initSettings(it)
		})
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentSettingsEditInfoBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@SettingsEditInfoFragment
				viewModel = sharedViewModel
				executePendingBindings()
			}.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		
		binding.rvSettingsEditPhotos.apply {
			layoutManager = GridLayoutManager(this.context, 2, GridLayoutManager.VERTICAL, false)
			adapter =  mEditorPhotoAdapter
			addItemDecoration(GridItemDecoration())
		}

		mEditorPhotoAdapter.setOnItemClickListener(object: SettingsEditInfoPhotoAdapter.OnItemClickListener {

			override fun onItemClick(view: View, position: Int) {
				if (mEditorPhotoAdapter.itemCount > 1) {
					val photoToDelete = mEditorPhotoAdapter.getItem(position)

					val isMainPhotoDeleting = photoToDelete.fileUrl == currentUser.baseUserInfo.mainPhotoUrl

					//deletion observer
					mViewModel.photoDeletingStatus.observeOnce(this@SettingsEditInfoFragment, {
						if (it) {
							currentUser.photoURLs.remove(photoToDelete)
							mEditorPhotoAdapter.removeAt(position)

							if (isMainPhotoDeleting) {
								currentUser.baseUserInfo.mainPhotoUrl = currentUser.photoURLs[0].fileUrl
							}
						}
					})
					//execute deleting
					mViewModel.deletePhoto(photoToDelete,
					                                currentUser, isMainPhotoDeleting)
				}
				else requireContext().showToastText(getString(R.string.toast_text_at_least_1_photo_required))
			}
		})

		//touch event guarantee that if user want to scroll or touch outside of edit box
		//keyboard hide and editText focus clear
		binding.containerScrollSettings.setOnTouchListener { v, _ ->
			v.performClick()
			val iMM = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			iMM.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
			binding.edSettingsEditDescription.clearFocus()

			return@setOnTouchListener false
		}
		
		binding.btnSettingsEditGenderMale.setOnClickListener {
			gender = male
			currentUser.baseUserInfo.gender = gender
			binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderMale)
		}
		
		binding.btnSettingsEditGenderFemale.setOnClickListener {
			gender = female
			currentUser.baseUserInfo.gender = gender
			binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderFemale)
		}
		
		
		binding.btnSettingsEditSave.setOnClickListener {
			mViewModel.updateUserItem(currentUser)
		}
		binding.btnSettingsEditDelete.setOnClickListener {
			showDialogDeleteAttention()
		}
	}


	private fun initSettings(userItem: UserItem) {
		if (userItem.baseUserInfo.gender == male)
			binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderMale)
		else binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderFemale)

		changerNameSetup()
		changerAgeSetup()
		changerDescriptionSetup()

	}

	private fun changerNameSetup() {
		binding.edSettingsEditName.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				binding.layoutSettingsEditName.isCounterEnabled = true
			}

			override fun afterTextChanged(s: Editable) {
				when {
					s.length > binding.layoutSettingsEditName.counterMaxLength -> {
						binding.layoutSettingsEditName.error = getString(R.string.text_max_length_error)
						binding.btnSettingsEditSave.isEnabled = false
					}
					s.isEmpty() -> {
						binding.layoutSettingsEditName.error = getString(R.string.text_empty_error)
						binding.btnSettingsEditSave.isEnabled = false

					}
					else -> {
						binding.layoutSettingsEditName.error = ""
						name = s.toString().trim()
						currentUser.baseUserInfo.name = name
						binding.btnSettingsEditSave.isEnabled = true
					}
				}
			}
		})
		
		binding.edSettingsEditName.setOnEditorActionListener { editText, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				editText.text = editText.text.toString().trim()
				editText.clearFocus()
			}
			return@setOnEditorActionListener false
		}
	}

	private fun changerAgeSetup() {
		binding.sliderSettingsEditAge.addOnChangeListener { _, value, _ ->
			age = value.toInt()
			currentUser.baseUserInfo.age = age
			binding.tvSettingsEditAge.text = "Age: $age"
		}
	}

	private fun changerDescriptionSetup() {
		
		binding.edSettingsEditDescription.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				binding.layoutSettingsEditDescription.isCounterEnabled = true
			}

			override fun afterTextChanged(s: Editable) {
				if (s.length > binding.layoutSettingsEditDescription.counterMaxLength){
					binding.layoutSettingsEditDescription.error = getString(R.string.text_max_length_error)
					binding.btnSettingsEditSave.isEnabled = false
				}
				else {
					binding.layoutSettingsEditDescription.error = ""
					descriptionText = s.toString().trim()
					currentUser.aboutText = descriptionText
					binding.btnSettingsEditSave.isEnabled = true
				}
			}
		})
		
		binding.edSettingsEditDescription.setOnEditorActionListener { editText, actionId, _ ->
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				editText.text = editText.text.toString().trim()
				editText.clearFocus()
			}
			return@setOnEditorActionListener false
		}
		
		binding.edSettingsEditDescription.setOnTouchListener { view, event ->
			view.performClick()
			view.parent.requestDisallowInterceptTouchEvent(true)
			if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
				view.parent.requestDisallowInterceptTouchEvent(false)
			}
			return@setOnTouchListener false
		}

	}

	private fun showDialogDeleteAttention() = MaterialAlertDialogBuilder(requireContext())
		.setTitle(R.string.dialog_profile_delete_title)
		.setMessage(R.string.dialog_profile_delete_message)
		.setPositiveButton(R.string.dialog_delete_btn_positive_text) { dialog, which ->
			mViewModel.selfDeletingStatus.value = IN_PROGRESS
			mViewModel.deleteMyAccount()
		}
		.setNegativeButton(R.string.dialog_delete_btn_negative_text, null)
		.create()
		.show()

	override fun onBackPressed() {
		super.onBackPressed()
		navController.navigateUp()
	}

}