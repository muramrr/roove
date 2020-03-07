/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 19:14
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.core.UserItem
import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.roove.BuildConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.permissions.AppPermission
import com.mmdev.roove.core.permissions.handlePermission
import com.mmdev.roove.core.permissions.onRequestPermissionsResultReceived
import com.mmdev.roove.core.permissions.requestAppPermissions
import com.mmdev.roove.databinding.FragmentSettingsBinding
import com.mmdev.roove.ui.SharedViewModel
import com.mmdev.roove.ui.auth.AuthViewModel
import com.mmdev.roove.ui.common.LifecycleStates
import com.mmdev.roove.ui.common.base.BaseAdapter
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.common.custom.CenterFirstLastItemDecoration
import com.mmdev.roove.ui.common.custom.HorizontalCarouselLayoutManager
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.ui.profile.view.PlacesToGoAdapter
import com.mmdev.roove.utils.observeOnce
import com.mmdev.roove.utils.showToastText
import kotlinx.android.synthetic.main.fragment_settings.*
import java.io.File
import java.util.*


/**
 * This is the documentation block about the class
 */

class SettingsFragment: BaseFragment(R.layout.fragment_settings) {

	private val mSettingsPhotoAdapter =
		SettingsUserPhotoAdapter(mutableListOf(), R.layout.fragment_settings_photo_item)

	private val mPlacesToGoAdapter =
		PlacesToGoAdapter(listOf(), R.layout.fragment_profile_places_rv_item)

	private lateinit var authViewModel: AuthViewModel
	private lateinit var remoteRepoViewModel: RemoteRepoViewModel
	private lateinit var sharedViewModel: SharedViewModel

	private lateinit var userItem: UserItem

	// File
	private lateinit var mFilePathImageCamera: File

	companion object {
		private const val IMAGE_GALLERY_REQUEST = 1
		private const val IMAGE_CAMERA_REQUEST = 2
		private const val PLACE_ID_KEY = "PLACE_ID"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.run {
			authViewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]
			remoteRepoViewModel = ViewModelProvider(this, factory)[RemoteRepoViewModel::class.java]
			sharedViewModel = ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")

		sharedViewModel.getCurrentUser().observeOnce(this, Observer {
			userItem = it
		})

		remoteRepoViewModel.photoUrls.observe(this, Observer {
			mSettingsPhotoAdapter.setData(it)
		})

		sharedViewModel.modalBottomSheetStatus.observeOnce(this, Observer{
			if (it == LifecycleStates.STOP) remoteRepoViewModel.updateUserItem(userItem)
		})
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentSettingsBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@SettingsFragment
				viewModel = sharedViewModel
				executePendingBindings()
			}.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		toolbarSettings.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.settings_action_preferences -> showModalBottomSheet()
				R.id.settings_action_log_out -> showSignOutPrompt()
			}
			return@setOnMenuItemClickListener true
		}

		rvSettingsUserPhotosList.apply {
			adapter = mSettingsPhotoAdapter
			layoutManager = HorizontalCarouselLayoutManager(this.context, HORIZONTAL, false)
			//item decorator to make first and last item align center
			addItemDecoration(CenterFirstLastItemDecoration())
			//adjust auto swipe to item center
			val snapHelper: SnapHelper = LinearSnapHelper()
			snapHelper.attachToRecyclerView(this)
		}

		fabSettingsAddPhoto.setOnClickListener {
			//show attachment dialog picker
			val builder = AlertDialog.Builder(it.context)
				.setItems(arrayOf("Camera", "Gallery")) {
					_, itemIndex ->
					if (itemIndex == 0) { photoCameraClick() }
					else { photoGalleryClick() }
				}
			val alertDialog = builder.create()
			val params = alertDialog.window?.attributes
			params?.gravity = Gravity.CENTER
			alertDialog.show()
		}


		rvSettingsWantToGoList.apply { adapter = mPlacesToGoAdapter }

		mPlacesToGoAdapter.setOnItemClickListener(object: BaseAdapter.OnItemClickListener<BasePlaceInfo> {

			override fun onItemClick(item: BasePlaceInfo, position: Int) {
				val placeId = bundleOf(PLACE_ID_KEY to item.id)
				findNavController().navigate(R.id.action_settings_to_placeDetailedFragment, placeId)
			}
		})


		fabSettingsEdit.setOnClickListener {
			findNavController().navigate(R.id.action_settings_to_settingsEditInfoFragment)
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

	private fun showModalBottomSheet() {
		val modalBottomSheet = SettingsModalBottomSheet.newInstance(true)
		modalBottomSheet.show(childFragmentManager, SettingsModalBottomSheet::class.java.canonicalName)
	}

	/*
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 * else open camera intent
	 */
	private fun photoCameraClick() =
		handlePermission(AppPermission.CAMERA,
		                 onGranted = { startCameraIntent() },
		                 onDenied = { requestAppPermissions(it) },
		                 onExplanationNeeded = { it.explanationMessageId })

	//take photo directly by camera
	private fun startCameraIntent() {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
		mFilePathImageCamera = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
		                            userItem.baseUserInfo.name + namePhoto + "camera.jpg")
		val photoURI = FileProvider.getUriForFile(context!!,
		                                          BuildConfig.APPLICATION_ID + ".provider",
		                                          mFilePathImageCamera)
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
	private fun photoGalleryClick() =
		handlePermission(AppPermission.GALLERY,
		                 onGranted = { startGalleryIntent() },
		                 onDenied = { requestAppPermissions(it) },
		                 onExplanationNeeded = { it.explanationMessageId })

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
				remoteRepoViewModel.uploadUserProfilePhoto(selectedUri.toString(), userItem)
			}
		}
		// send photo taken by camera
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					remoteRepoViewModel.uploadUserProfilePhoto(Uri.fromFile(mFilePathImageCamera).toString(),
					                                           userItem)
				}
				else context?.showToastText("filePathImageCamera is null or filePathImageCamera isn't exists")

			}
		}
	}



}