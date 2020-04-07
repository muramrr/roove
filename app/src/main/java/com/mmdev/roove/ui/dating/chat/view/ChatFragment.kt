/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.04.20 14:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.chat.view

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.chat.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
import com.mmdev.business.pairs.MatchedUserItem
import com.mmdev.business.remote.entity.Report
import com.mmdev.business.remote.entity.Report.ReportType.*
import com.mmdev.roove.BuildConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.glide.GlideApp
import com.mmdev.roove.core.permissions.AppPermission
import com.mmdev.roove.core.permissions.handlePermission
import com.mmdev.roove.core.permissions.onRequestPermissionsResultReceived
import com.mmdev.roove.core.permissions.requestAppPermissions
import com.mmdev.roove.databinding.FragmentChatBinding
import com.mmdev.roove.ui.common.base.BaseFragment
import com.mmdev.roove.ui.dating.chat.ChatViewModel
import com.mmdev.roove.ui.profile.RemoteRepoViewModel
import com.mmdev.roove.utils.EndlessRecyclerViewScrollListener
import com.mmdev.roove.utils.observeOnce
import com.mmdev.roove.utils.showMaterialAlertDialogPicker
import com.mmdev.roove.utils.showToastText
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.util.*


/**
 * This is the documentation block about the class
 */

class ChatFragment : BaseFragment<ChatViewModel>() {

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
		associatedViewModel = getViewModel()
		remoteRepoViewModel = ViewModelProvider(this, factory)[RemoteRepoViewModel::class.java]

		//deep link from notification
		arguments?.let {
			receivedPartnerCity = it.getString(PARTNER_CITY_KEY, "")
			receivedPartnerGender = it.getString(PARTNER_GENDER_KEY, "")
			receivedPartnerId = it.getString(PARTNER_ID_KEY, "")
			receivedConversationId = it.getString(CONVERSATION_ID_KEY, "")
			if (receivedPartnerCity.isNotEmpty() &&
			    receivedPartnerGender.isNotEmpty() &&
                receivedPartnerId.isNotEmpty() &&
                receivedConversationId.isNotEmpty()) isDeepLinkJump = true
		}

		sharedViewModel.getCurrentUser().observeOnce(this, Observer {
			//observe current user id to understand left/right message
			mChatAdapter.setCurrentUserId(it.baseUserInfo.userId)
			currentUser = it
		})


		//if it was a deep link navigation then create ConversationItem "on a flight"
		if (isDeepLinkJump) {
			sharedViewModel.matchedUserItemSelected.value =
				MatchedUserItem(baseUserInfo = BaseUserInfo(city = receivedPartnerCity,
				                                            gender = receivedPartnerGender,
				                                            userId = receivedPartnerId),
				                conversationId = receivedConversationId,
				                conversationStarted = true)
			sharedViewModel.conversationSelected.value =
				ConversationItem(partner = BaseUserInfo(city = receivedPartnerCity,
				                                        gender = receivedPartnerGender,
				                                        userId = receivedPartnerId),
				                 conversationId = receivedConversationId,
				                 conversationStarted = true)
		}
		//setup observer
		remoteRepoViewModel.retrievedUserItem.observeOnce(this, Observer {
			currentPartner = it
			setupContentToolbar(it)
		})
		associatedViewModel.newMessage.observe(this, Observer {
			mChatAdapter.newMessage()
			rvMessageList.scrollToPosition(0)
		})

		remoteRepoViewModel.reportSubmittingStatus.observeOnce(this, Observer {
			isReported = it
			context?.showToastText(getString(R.string.toast_text_report_success))
		})

		//ready? steady? init dialog_loading.
		sharedViewModel.conversationSelected.observeOnce(this, Observer {
			currentConversation = it
			remoteRepoViewModel.getRequestedUserInfo(it.partner)
			associatedViewModel.loadMessages(it)
			associatedViewModel.observeNewMessages(it)
		})

	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentChatBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ChatFragment
				viewModel = associatedViewModel
				executePendingBindings()
			}
			.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		edTextMessageInput.addTextChangedListener(object: TextWatcher{
			override fun afterTextChanged(s: Editable?) {}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				btnSendMessage.isActivated = s.trim().isNotEmpty()
			}
		})

		btnSendMessage.setOnClickListener { sendMessageClick() }

		//show attachment dialog picker
		btnSendAttachment.setOnClickListener {
			val materialDialogPicker =
				it.context.showMaterialAlertDialogPicker(listOf({ photoCameraClick() },
				                                                { photoGalleryClick() }))
			val params = materialDialogPicker.window?.attributes
			params?.gravity = Gravity.BOTTOM
			materialDialogPicker.show()
		}

		//if message contains photo then it opens in fullscreen dialog
		mChatAdapter.apply {
			setOnAttachedPhotoClickListener(object: ChatAdapter.OnItemClickListener {
				override fun onItemClick(view: View, position: Int) {

					val photoUrl = mChatAdapter.getItem(position).photoItem!!.fileUrl
					val dialog = FullScreenDialogFragment.newInstance(photoUrl)
					dialog.show(childFragmentManager, FullScreenDialogFragment::class.java.canonicalName)

				}
			})
		}

		val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
		rvMessageList.apply {
			adapter = mChatAdapter
			layoutManager = linearLayoutManager
			//touch event guarantee that if user want to scroll or touches recycler with messages
			//keyboard hide and editText focus clear
			setOnTouchListener { v, _ ->
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
						associatedViewModel.loadMoreMessages()
					}

				}
			})
		}

		toolbarChat.setNavigationOnClickListener { navController.navigateUp() }

		toolbarChat.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.chat_action_user -> {
					navController.navigate(R.id.action_chat_to_profileFragment)
				}

				R.id.chat_action_report -> { if (!isReported) showReportDialog() }
			}
			return@setOnMenuItemClickListener true
		}
	}

	override fun onResume() {
		// returns to fragment from onStop
		// if user clicks on toolbar partner icon this fragment will not be destroyed
		// onCreate is no longer being called in this scenario
		super.onResume()
		if (this::currentPartner.isInitialized ) { setupContentToolbar(currentPartner) }
	}

	private fun setupContentToolbar(partnerUserItem: UserItem){
		toolbarChat.apply {
			//menu declared directly in xml
			//no need to inflate menu manually
			//set only title, actions and icon

			val partnerIcon = menu.findItem(R.id.chat_action_user)
			if (partnerUserItem.baseUserInfo.mainPhotoUrl.isNotEmpty())
				GlideApp.with(this)
					.load(partnerUserItem.baseUserInfo.mainPhotoUrl)
					.centerCrop()
					.apply(RequestOptions().circleCrop())
					.into(object : CustomTarget<Drawable>(){
						override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
							partnerIcon.icon = resource
						}
						override fun onLoadCleared(placeholder: Drawable?) {}
					})

			title = partnerUserItem.baseUserInfo.name
		}
	}

	/*
	* Send plain text msg to chat if editText is not empty
	* else shake animation
	*/
	private fun sendMessageClick() {
		if (edTextMessageInput.text.toString().trim().isNotEmpty()) {

			val message =
				MessageItem(sender = currentUser.baseUserInfo,
				            recipientId = currentConversation.partner.userId,
				            text = edTextMessageInput.text.toString().trim(),
				            photoItem = null,
				            conversationId = currentConversation.conversationId)

			associatedViewModel.sendMessage(message)
			rvMessageList.scrollToPosition(0)
			edTextMessageInput.text.clear()
		}
		else edTextMessageInput.startAnimation(
				AnimationUtils.loadAnimation(context, R.anim.horizontal_shake))

	}

	/*
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 * else open camera intent
	 */
	private fun photoCameraClick() {
		handlePermission(AppPermission.CAMERA,
		                 onGranted = { startCameraIntent() },
		                 onDenied = { requestAppPermissions(it) },
		                 onExplanationNeeded = {
			                 /** Additional explanation for permission usage needed **/
		                 }
		)
	}

	//take photo directly by camera
	private fun startCameraIntent() {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
		mFilePathImageCamera = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
		                            namePhoto + "camera.jpg")
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
	private fun photoGalleryClick() {
		handlePermission(AppPermission.GALLERY,
		                 onGranted = { startGalleryIntent() },
		                 onDenied = { requestAppPermissions(it) },
		                 onExplanationNeeded = {
			                 /** Additional explanation for permission usage needed **/
		                 }
		)
	}

	//open gallery chooser
	private fun startGalleryIntent() {
		val intent = Intent().apply {
			action = Intent.ACTION_GET_CONTENT
			type = "image/*"
		}
		startActivityForResult(Intent.createChooser(intent, "Select image"),
		                       IMAGE_GALLERY_REQUEST)
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
					context?.showToastText(getString(it.deniedMessageId))
				})
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// send photo from gallery
		if (requestCode == IMAGE_GALLERY_REQUEST) {
			if (resultCode == RESULT_OK) {

				val selectedUri = data?.data
				associatedViewModel.sendPhoto(photoUri = selectedUri.toString(),
				                              conversation = currentConversation,
				                              sender = currentUser.baseUserInfo)
			}
		}
		// send photo taken by camera
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					associatedViewModel.sendPhoto(photoUri = Uri.fromFile(mFilePathImageCamera).toString(),
					                              conversation = currentConversation,
					                              sender = currentUser.baseUserInfo)
				}
				else context?.showToastText("filePathImageCamera is null or filePathImageCamera isn't exists")
			}
		}
	}

	private fun showReportDialog() {
		val materialDialogPicker = MaterialAlertDialogBuilder(context)
			.setItems(arrayOf(getString(R.string.report_chooser_photos),
			                  getString(R.string.report_chooser_behavior),
			                  getString(R.string.report_chooser_fake))) {
				_, itemIndex ->
				when (itemIndex) {
					0 -> { remoteRepoViewModel.submitReport(Report(INELIGIBLE_PHOTOS,
					                                               currentPartner.baseUserInfo)) }
					1 -> { remoteRepoViewModel.submitReport(Report(DISRESPECTFUL_BEHAVIOR,
					                                               currentPartner.baseUserInfo)) }
					2 -> { remoteRepoViewModel.submitReport(Report(FAKE, currentPartner.baseUserInfo)) }
				}
			}
			.create()
		val params = materialDialogPicker.window?.attributes
		params?.gravity = Gravity.CENTER
		materialDialogPicker.show()
	}


	override fun onBackPressed() {
		navController.navigateUp()
	}


}
