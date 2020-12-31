/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:24
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat.view

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.chat.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.remote.Report
import com.mmdev.business.remote.Report.ReportType.*
import com.mmdev.business.user.BaseUserInfo
import com.mmdev.business.user.UserItem
import com.mmdev.roove.BuildConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.permissions.AppPermission
import com.mmdev.roove.core.permissions.handlePermission
import com.mmdev.roove.core.permissions.onRequestPermissionsResultReceived
import com.mmdev.roove.core.permissions.requestAppPermissions
import com.mmdev.roove.databinding.FragmentChatBinding
import com.mmdev.roove.ui.chat.ChatViewModel
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import com.mmdev.roove.utils.extensions.observeOnce
import com.mmdev.roove.utils.extensions.showToastText
import java.io.File
import java.util.*


/**
 * This is the documentation block about the class
 */

class ChatFragment : BaseFragment<ChatViewModel, FragmentChatBinding>(
	layoutId = R.layout.fragment_chat
) {

	private lateinit var currentUser: UserItem

	private var receivedPartnerCity = ""
	private var receivedPartnerGender = ""
	private var receivedPartnerId = ""
	private var receivedConversationId = ""

	private var isDeepLinkJump: Boolean = false
	private var isReported: Boolean = false

	private lateinit var currentConversation: ConversationItem
	private lateinit var currentPartner: UserItem

	private val mChatAdapter: ChatAdapter = ChatAdapter()

	// File
	private lateinit var mFilePathImageCamera: File

	private lateinit var remoteRepoViewModel: RemoteRepoViewModel



	//static fields
	companion object {

		private const val IMAGE_GALLERY_REQUEST = 1
		private const val IMAGE_CAMERA_REQUEST = 2


		private const val PARTNER_CITY_KEY = "PARTNER_CITY"
		private const val PARTNER_GENDER_KEY = "PARTNER_GENDER"
		private const val PARTNER_ID_KEY = "PARTNER_ID"
		private const val CONVERSATION_ID_KEY = "CONVERSATION_ID"

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = getViewModel()
		remoteRepoViewModel = ViewModelProvider(this, factory)[RemoteRepoViewModel::class.java]

		//deep link from notification
		arguments?.let {
			receivedPartnerCity = it.getString(PARTNER_CITY_KEY, "")
			receivedPartnerGender = it.getString(PARTNER_GENDER_KEY, "")
			receivedPartnerId = it.getString(PARTNER_ID_KEY, "")
			receivedConversationId = it.getString(CONVERSATION_ID_KEY, "")
			if (receivedPartnerCity.isNotEmpty() && receivedPartnerGender.isNotEmpty() && receivedPartnerId.isNotEmpty() && receivedConversationId.isNotEmpty()) isDeepLinkJump = true
		}

		sharedViewModel.getCurrentUser().observeOnce(this, {
			//observe current user id to understand left/right message
			mChatAdapter.setCurrentUserId(it.baseUserInfo.userId)
			currentUser = it
		})


		//if it was a deep link navigation then create ConversationItem "on a flight"
		if (isDeepLinkJump) {
			sharedViewModel.matchedUserItemSelected.value =
				MatchedUserItem(
					baseUserInfo = BaseUserInfo(
						city = receivedPartnerCity,
						gender = receivedPartnerGender,
						userId = receivedPartnerId
					),
					conversationId = receivedConversationId,
					conversationStarted = true
				)
			sharedViewModel.conversationSelected.value =
				ConversationItem(
					partner = BaseUserInfo(
						city = receivedPartnerCity,
						gender = receivedPartnerGender,
						userId = receivedPartnerId
					),
					conversationId = receivedConversationId,
					conversationStarted = true
				)
		}
		//setup
		remoteRepoViewModel.retrievedUserItem.observeOnce(this, {
			currentPartner = it
			mViewModel.partnerName.value = it.baseUserInfo.name.split(" ")[0]
			mViewModel.partnerPhoto.value = it.baseUserInfo.mainPhotoUrl
		})

		//todo: observe new messages
		//mViewModel.newMessage.observe(this, {
		//	mChatAdapter.newMessage()
		//	rvMessageList.scrollToPosition(0)
		//})

		remoteRepoViewModel.reportSubmittingStatus.observeOnce(this, {
			isReported = it
			requireContext().showToastText(getString(R.string.toast_text_report_success))
		})

		//ready? steady? init dialog_loading.
		sharedViewModel.conversationSelected.observeOnce(this, {
			currentConversation = it
			remoteRepoViewModel.getRequestedUserInfo(it.partner)
			mViewModel.observeNewMessages(it)
			mViewModel.observePartnerOnline(it.conversationId)
			mViewModel.loadMessages(it)
		})

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = binding.run {

		edTextMessageInput.doOnTextChanged { text, start, before, count ->
			btnSendMessage.isActivated = text?.trim().isNullOrBlank()
		}

		btnSendMessage.setOnClickListener { sendMessageClick() }

		//show attachment dialog picker
		btnSendAttachment.setOnClickListener {
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
				.apply { window?.attributes?.gravity = Gravity.BOTTOM }
				.show()
		}

		//if message contains photo then it opens in fullscreen dialog
		mChatAdapter.setOnAttachedPhotoClickListener { view, position ->
			val photoUrl = mChatAdapter.getItem(position).photoItem!!.fileUrl
			FullScreenDialogFragment
				.newInstance(photoUrl)
				.show(childFragmentManager, FullScreenDialogFragment::class.java.canonicalName)
		}
		

		val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
		rvMessageList.apply {
			adapter = mChatAdapter
			layoutManager = linearLayoutManager
			//touch event guarantee that if user want to scroll or touches recycler with messages
			//keyboard hide and editText focus clear
			setOnTouchListener { v, _ ->
				v.performClick()
				val iMM = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				iMM.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
				edTextMessageInput.clearFocus()

				return@setOnTouchListener false
			}
			//load more messages on scroll
			addOnScrollListener(object: EndlessRecyclerViewScrollListener(linearLayoutManager) {
				override fun onLoadMore(page: Int, totalItemsCount: Int) {

					if (linearLayoutManager.findLastVisibleItemPosition() == totalItemsCount - 4){
						//Log.wtf(TAG, "load seems to be called")
						mViewModel.loadMoreMessages()
					}

				}
			})
		}

		toolbarChat.setNavigationOnClickListener { navController.navigateUp() }

		toolbarChat.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.chat_action_report -> { if (!isReported) showReportDialog() }
			}
			return@setOnMenuItemClickListener true
		}

		toolbarChat.setOnClickListener {
			navController.navigate(R.id.action_chat_to_profileFragment)
		}
	}

	/*
	* Send plain text msg to chat if editText is not empty
	* else shake animation
	*/
	private fun sendMessageClick() = binding.run {
		if (edTextMessageInput.text.toString().trim().isNotEmpty()) {

			val message = MessageItem(
				sender = currentUser.baseUserInfo,
				recipientId = currentConversation.partner.userId,
				text = edTextMessageInput.text.toString().trim(),
				photoItem = null,
				conversationId = currentConversation.conversationId
			)

			mViewModel.sendMessage(message)
			rvMessageList.scrollToPosition(0)
			edTextMessageInput.text.clear()
		}
		else edTextMessageInput.startAnimation(AnimationUtils.loadAnimation(context, R.anim.horizontal_shake))

	}

	/*
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 * else open camera intent
	 */
	private fun photoCameraClick() = handlePermission(
		AppPermission.CAMERA,
		onGranted = { startCameraIntent() },
		onDenied = { requestAppPermissions(it) },
		onExplanationNeeded = {
			/** Additional explanation for permission usage needed **/
		}
	)

	//take photo directly by camera
	private fun startCameraIntent() {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
		mFilePathImageCamera = File(
			requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), namePhoto + "camera.jpg")
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
		onExplanationNeeded = {
			/** Additional explanation for permission usage needed **/
		}
	)
	

	//open gallery chooser
	private fun startGalleryIntent() {
		val intent = Intent().apply {
			action = Intent.ACTION_GET_CONTENT
			type = "image/*"
		}
		startActivityForResult(Intent.createChooser(intent, "Select image"), IMAGE_GALLERY_REQUEST)
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
				onPermissionDenied = {
					requireContext().showToastText(getString(it.deniedMessageId))
				}
		)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// send photo from gallery
		if (requestCode == IMAGE_GALLERY_REQUEST) {
			if (resultCode == RESULT_OK) {

				val selectedUri = data?.data
				mViewModel.sendPhoto(photoUri = selectedUri.toString(),
				                              conversation = currentConversation,
				                              sender = currentUser.baseUserInfo)
			}
		}
		// send photo taken by camera
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					mViewModel.sendPhoto(
						photoUri = Uri.fromFile(mFilePathImageCamera).toString(),
						conversation = currentConversation,
						sender = currentUser.baseUserInfo
					)
				}
				else requireContext().showToastText(
					"filePathImageCamera is null or filePathImageCamera isn't exists"
				)
			}
		}
	}

	private fun showReportDialog() = MaterialAlertDialogBuilder(requireContext())
		.setItems(
			arrayOf(
				getString(R.string.report_chooser_photos),
				getString(R.string.report_chooser_behavior),
				getString(R.string.report_chooser_fake)
			)
		) { _, itemIndex ->
			when (itemIndex) {
				0 -> {
					remoteRepoViewModel.submitReport(Report(INELIGIBLE_PHOTOS, currentPartner.baseUserInfo))
				}
				1 -> {
					remoteRepoViewModel.submitReport(Report(DISRESPECTFUL_BEHAVIOR, currentPartner.baseUserInfo))
				}
				2 -> {
					remoteRepoViewModel.submitReport(Report(FAKE, currentPartner.baseUserInfo))
				}
			}
		}
		.create()
		.apply { window?.attributes?.gravity = Gravity.CENTER }
		.show()
	

	override fun onBackPressed() {
		navController.navigateUp()
	}

}
