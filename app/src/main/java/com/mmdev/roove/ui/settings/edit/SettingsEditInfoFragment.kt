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
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.domain.user.data.Gender
import com.mmdev.domain.user.data.Gender.FEMALE
import com.mmdev.domain.user.data.Gender.MALE
import com.mmdev.domain.user.data.UserItem
import com.mmdev.roove.R
import com.mmdev.roove.databinding.FragmentSettingsEditInfoBinding
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.GridItemDecoration
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.ui.profile.RemoteRepoViewModel.DeletingStatus.IN_PROGRESS
import com.mmdev.roove.utils.extensions.hideKeyboard
import com.mmdev.roove.utils.extensions.showToastText

/**
 * This fragment allow you to edit your profile
 */

class SettingsEditInfoFragment: BaseFragment<RemoteRepoViewModel, FragmentSettingsEditInfoBinding>(
	layoutId = R.layout.fragment_settings_edit_info
) {
	
	override val mViewModel: RemoteRepoViewModel by requireParentFragment().viewModels()
	
	private val mEditorPhotoAdapter = SettingsEditInfoPhotoAdapter()

	private var newName: String? = null
	private var newAge: Int? = null
	private var newGender: Gender? = null
	private var newDescription: String? = null

	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		initProfile(MainActivity.currentUser!!)
		
		binding.rvSettingsEditPhotos.apply {
			layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
			adapter =  mEditorPhotoAdapter
			addItemDecoration(GridItemDecoration())
		}

		mEditorPhotoAdapter.setOnItemClickListener { item , position ->
			if (mEditorPhotoAdapter.itemCount > 1) {
				val isMainPhotoDeleting = item.fileUrl == MainActivity.currentUser!!.baseUserInfo.mainPhotoUrl
				
				//deletion observer //todo
				//mViewModel.photoDeletingStatus.observeOnce(this@SettingsEditInfoFragment, {
				//	if (it) {
				//		currentUser.photoURLs.minus(item)
				//		mEditorPhotoAdapter.removeAt(position)
				//
				//		if (isMainPhotoDeleting) {
				//			currentUser.baseUserInfo.copy(
				//				mainPhotoUrl = currentUser.photoURLs[0].fileUrl
				//			)
				//		}
				//	}
				//})
				//execute deleting
				mViewModel.deletePhoto(item, isMainPhotoDeleting)
			}
			else requireContext().showToastText(getString(R.string.toast_text_at_least_1_photo_required))
			
		}

		//touch event guarantee that if user want to scroll or touch outside of edit box
		//keyboard hide and editText focus clear
		binding.root.setOnTouchListener { v, _ ->
			v.performClick()
			v.hideKeyboard(binding.edSettingsEditDescription)
			return@setOnTouchListener false
		}
		
		binding.toggleButtonSettingsEditGender.addOnButtonCheckedListener { _, checkedId, isChecked ->
			when {
				checkedId == binding.btnSettingsEditGenderMale.id && isChecked -> { newGender = MALE }
				checkedId == binding.btnSettingsEditGenderFemale.id && isChecked -> { newGender = FEMALE }
			}
		}
		
		//todo
		binding.btnSettingsEditSave.setOnClickListener {
			//mViewModel.updateUserItem()
		}
		binding.btnSettingsEditDelete.setOnClickListener {
			showDialogDeleteAttention()
		}
	}


	private fun initProfile(userItem: UserItem) {
		if (userItem.baseUserInfo.gender == MALE)
			binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderMale)
		else binding.toggleButtonSettingsEditGender.check(R.id.btnSettingsEditGenderFemale)

		changerNameSetup()
		changerAgeSetup()
		changerDescriptionSetup()

	}

	private fun changerNameSetup() = binding.edSettingsEditName.run {
		doAfterTextChanged {
			when {
				it.isNullOrBlank() -> {
					binding.layoutSettingsEditName.error = getString(R.string.text_empty_error)
					binding.btnSettingsEditSave.isEnabled = false
				}
				
				else -> {
					binding.layoutSettingsEditName.error = ""
					newName = it.toString().trim()
					binding.btnSettingsEditSave.isEnabled = true
				}
			}
		}
	}

	//todo
	private fun changerAgeSetup() {
		binding.sliderSettingsEditAge.addOnChangeListener { _, value, _ ->
			newAge = value.toInt()
			//currentUser.baseUserInfo.age = age
			binding.tvSettingsEditAge.text = "Age: $newAge"
		}
	}
	
	private fun changerDescriptionSetup() = binding.edSettingsEditDescription.run {
		
		doOnTextChanged { text, start, before, count ->
			newDescription = text.toString().trim()
		}
		
//		setOnTouchListener { view, event ->
//			view.performClick()
//			view.parent.requestDisallowInterceptTouchEvent(true)
//			if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
//				view.parent.requestDisallowInterceptTouchEvent(false)
//			}
//			return@setOnTouchListener false
//		}

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