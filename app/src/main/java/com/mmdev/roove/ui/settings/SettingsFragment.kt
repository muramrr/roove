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

package com.mmdev.roove.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.roove.BuildConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.permissions.AppPermission
import com.mmdev.roove.core.permissions.AppPermission.PermissionCode
import com.mmdev.roove.core.permissions.handlePermission
import com.mmdev.roove.core.permissions.onRequestPermissionsResultReceived
import com.mmdev.roove.core.permissions.requestAppPermissions
import com.mmdev.roove.databinding.FragmentSettingsBinding
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.CenterFirstLastItemDecoration
import com.mmdev.roove.ui.common.custom.HorizontalCarouselLayoutManager
import com.mmdev.roove.ui.settings.edit.SettingsPreferencesBottomSheet
import com.mmdev.roove.utils.extensions.showToastText
import com.mmdev.roove.utils.extensions.visible
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

/**
 * Shows your profile, allows to edit preferences or signOut
 */

@AndroidEntryPoint
class SettingsFragment: BaseFragment<SettingsViewModel, FragmentSettingsBinding>(
	layoutId = R.layout.fragment_settings
) {
	
	override val mViewModel: SettingsViewModel by viewModels()

	private val mSettingsPhotoAdapter = SettingsUserPhotoAdapter()

	// File
	private lateinit var mFilePathImageCamera: File

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		mViewModel.newPhoto.observe(this, {
			mSettingsPhotoAdapter.newAdded(it)
		})

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {
		
		mSettingsPhotoAdapter.setData(MainActivity.currentUser!!.photoURLs)
		
		toolbarSettings.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.settings_action_preferences -> showModalBottomSheet()
				R.id.settings_action_log_out -> showSignOutPrompt()
			}
			return@setOnMenuItemClickListener true
		}

		rvSettingsUserPhotosList.run {
			adapter = mSettingsPhotoAdapter
			layoutManager = HorizontalCarouselLayoutManager(context, false)
			//adjust auto swipe to item center
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
			
			//item decorator to make first and last item align center
			addItemDecoration(CenterFirstLastItemDecoration(rootView.width / 2))
		}
		
		fabSettingsEdit.setOnClickListener {
			navController.navigate(R.id.action_settings_to_settingsEditInfoFragment)
		}

	}
	
	override fun onStart() {
		super.onStart()
		//refresh each time when user moves back from edit page
		setupUser()
	}
	
	private fun setupUser() = binding.run {
		if (MainActivity.currentUser!!.aboutText.isBlank()) tvSettingsNoAboutText.visible()
		
		tvSettingsAboutText.text = MainActivity.currentUser!!.aboutText
		
		tvSettingsNameAge.text = getString(R.string.name_age_formatter).format(
			MainActivity.currentUser!!.baseUserInfo.name,
			MainActivity.currentUser!!.baseUserInfo.age
		)
		
		if (MainActivity.currentUser!!.photoURLs.size != mSettingsPhotoAdapter.itemCount)
			mSettingsPhotoAdapter.setData(MainActivity.currentUser!!.photoURLs)
		
		//allow only 6 photos
		if (MainActivity.currentUser!!.photoURLs.size < 6) fabSettingsAddPhoto.setOnClickListener {
			MaterialAlertDialogBuilder(requireContext())
				.setItems(
					arrayOf(
						getString(R.string.material_dialog_picker_camera),
						getString(R.string.material_dialog_picker_gallery)
					)
				) { _, itemIndex ->
					when (itemIndex) {
						0 -> photoCameraClick()
						1 -> photoGalleryClick()
					}
				}
				.create()
				.apply { window?.attributes?.gravity = Gravity.CENTER }
				.show()
		}
	}
	

	/** log out pop up */
	private fun showSignOutPrompt() = MaterialAlertDialogBuilder(requireContext(), R.style.MyConfirmationMaterialAlertDialog)
		.setTitle(R.string.dialog_exit_title)
		.setMessage(R.string.dialog_exit_message)
		.setPositiveButton(R.string.dialog_exit_positive_btn_text) { _, _ -> sharedViewModel.logOut() }
		.setNegativeButton(R.string.dialog_exit_negative_btn_text, null)
		.create()
		.show()
		

	private fun showModalBottomSheet() = SettingsPreferencesBottomSheet().show(
		childFragmentManager,
		SettingsPreferencesBottomSheet::class.java.canonicalName
	)
	

	/**
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 * else open camera intent
	 */
	private fun photoCameraClick() = handlePermission(
		AppPermission.CAMERA,
		onGranted = { startCameraIntent() },
		onDenied = { requestAppPermissions(it) },
		onExplanationNeeded = { it.explanationMessageId }
	)

	//take photo directly by camera
	private fun startCameraIntent() {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
		mFilePathImageCamera = File(
			requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
			MainActivity.currentUser!!.baseUserInfo.name + namePhoto + "camera.jpg"
		)
		val photoURI = FileProvider.getUriForFile(
			requireContext(),
			BuildConfig.APPLICATION_ID + ".provider",
			mFilePathImageCamera
		)
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
			putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
		}
		startActivityForResult(intent, PermissionCode.REQUEST_CODE_CAMERA.code)
	}

	/**
	 * Checks if the app has permissions to READ user files
	 * If the app does not has permission then the user will be prompted to grant permissions
	 * else open gallery to choose photo
	 */
	private fun photoGalleryClick() = handlePermission(
		AppPermission.GALLERY,
		onGranted = { startGalleryIntent() },
		onDenied = { requestAppPermissions(it) },
		onExplanationNeeded = { it.explanationMessageId }
	)

	//open gallery chooser
	private fun startGalleryIntent() {
		val intent = Intent.createChooser(Intent().apply {
			action = Intent.ACTION_GET_CONTENT
			type = "image/*"
		}, "Select image")
		startActivityForResult(intent, PermissionCode.REQUEST_CODE_GALLERY.code)
	}

	// start after permissions was granted
	// If request is cancelled, the result arrays are empty.
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		onRequestPermissionsResultReceived(
			requestCode,
			grantResults,
			onPermissionGranted = {
				when (it) {
					AppPermission.CAMERA -> startCameraIntent()
					AppPermission.GALLERY -> startGalleryIntent()
					else -> {}
				}
			},
			onPermissionDenied = { it.deniedMessageId })
	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// send photo from gallery
		if (requestCode == PermissionCode.REQUEST_CODE_GALLERY.code) {
			if (resultCode == Activity.RESULT_OK) {

				val selectedUri = data?.data
				mViewModel.uploadUserProfilePhoto(selectedUri.toString())
			}
		}
		// send photo taken by camera
		if (requestCode == PermissionCode.REQUEST_CODE_CAMERA.code) {
			if (resultCode == Activity.RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					mViewModel.uploadUserProfilePhoto(Uri.fromFile(mFilePathImageCamera).toString(),)
				}
				else requireContext().showToastText(
					"filePathImageCamera is null or filePathImageCamera isn't exists"
				)

			}
		}
	}

}