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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.user.UserItem
import com.mmdev.roove.BuildConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.permissions.AppPermission
import com.mmdev.roove.core.permissions.handlePermission
import com.mmdev.roove.core.permissions.onRequestPermissionsResultReceived
import com.mmdev.roove.core.permissions.requestAppPermissions
import com.mmdev.roove.databinding.FragmentSettingsBinding
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.CenterFirstLastItemDecoration
import com.mmdev.roove.ui.common.custom.HorizontalCarouselLayoutManager
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.utils.extensions.observeOnce
import com.mmdev.roove.utils.extensions.showToastText
import java.io.File
import java.util.*


/**
 * This is the documentation block about the class
 */

class SettingsFragment: BaseFragment<SharedViewModel, FragmentSettingsBinding>(
	isViewModelActivityHosted = true
) {

	private lateinit var currentUser: UserItem

	private val mSettingsPhotoAdapter = SettingsUserPhotoAdapter()

	private lateinit var authViewModel: AuthViewModel
	private lateinit var remoteViewModel: RemoteRepoViewModel


	// File
	private lateinit var mFilePathImageCamera: File

	companion object {
		private const val IMAGE_GALLERY_REQUEST = 1
		private const val IMAGE_CAMERA_REQUEST = 2
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		mViewModel = sharedViewModel

		activity?.run {
			authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
			remoteViewModel = ViewModelProvider(this, factory)[RemoteRepoViewModel::class.java]
		} ?: throw Exception("Invalid Activity")
		
		remoteViewModel.photoUrls.observe(this, Observer {
			mSettingsPhotoAdapter.setData(it)
		})
		
		mViewModel.getCurrentUser().observeOnce(this, Observer {
			currentUser = it
		})
		
		mViewModel.modalBottomSheetNeedUpdateExecution.observe(this, Observer {
			if (it) {
				remoteViewModel.updateUserItem(currentUser)
				sharedViewModel.modalBottomSheetNeedUpdateExecution.value = false
			}
		})

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {

		toolbarSettings.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.settings_action_preferences -> showModalBottomSheet()
				R.id.settings_action_log_out -> showSignOutPrompt()
			}
			return@setOnMenuItemClickListener true
		}

		rvSettingsUserPhotosList.apply {
			adapter = mSettingsPhotoAdapter
			//item decorator to make first and last item align center
			addItemDecoration(CenterFirstLastItemDecoration())
			layoutManager = HorizontalCarouselLayoutManager(context, false)
			//adjust auto swipe to item center
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
		}

		fabSettingsAddPhoto.setOnClickListener {
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
		
		fabSettingsEdit.setOnClickListener {
			navController.navigate(R.id.action_settings_to_settingsEditInfoFragment)
		}

	}

	/** log out pop up*/
	private fun showSignOutPrompt() = MaterialAlertDialogBuilder(requireContext())
		.setTitle(R.string.dialog_exit_title)
		.setMessage(R.string.dialog_exit_message)
		.setPositiveButton(R.string.dialog_exit_positive_btn_text) { dialog, which ->
			authViewModel.logOut()
		}
		.setNegativeButton(R.string.dialog_exit_negative_btn_text, null)
		.create()
		.show()
		

	private fun showModalBottomSheet() = SettingsPreferencesBottomSheet().show(
		childFragmentManager,
		SettingsPreferencesBottomSheet::class.java.canonicalName
	)
	

	/*
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
			currentUser.baseUserInfo.name + namePhoto + "camera.jpg"
		)
		val photoURI = FileProvider.getUriForFile(
			requireContext(),
			BuildConfig.APPLICATION_ID + ".provider",
			mFilePathImageCamera
		)
		val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
			putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
		}
		startActivityForResult(intent, IMAGE_CAMERA_REQUEST)
	}

	/*
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
		startActivityForResult(intent, IMAGE_GALLERY_REQUEST)
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
				}
			},
			onPermissionDenied = { it.deniedMessageId })
	}


	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// send photo from gallery
		if (requestCode == IMAGE_GALLERY_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				val selectedUri = data?.data
				remoteViewModel.uploadUserProfilePhoto(selectedUri.toString(), currentUser)
			}
		}
		// send photo taken by camera
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					remoteViewModel.uploadUserProfilePhoto(
						Uri.fromFile(mFilePathImageCamera).toString(), currentUser
					)
				}
				else requireContext().showToastText(
					"filePathImageCamera is null or filePathImageCamera isn't exists"
				)

			}
		}
	}

}