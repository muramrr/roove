/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 12.03.20 18:25
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
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.core.BaseUserInfo
import com.mmdev.business.core.UserItem
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
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.util.*


/**
 * This is the documentation block about the class
 */

class ChatFragment : BaseFragment<ChatViewModel>(layoutId = R.layout.fragment_chat) {

	private lateinit var userItemModel: UserItem

	private var receivedPartnerCity = ""
	private var receivedPartnerGender = ""
	private var receivedPartnerId = ""
	private var receivedConversationId = ""

	private var isDeepLinkJump: Boolean = false

	private lateinit var currentConversation: ConversationItem
	private lateinit var currentPartner: UserItem

	private val mChatAdapter: ChatAdapter = ChatAdapter(listOf())

	// File
	private lateinit var mFilePathImageCamera: File

	private lateinit var remoteRepoViewModel: RemoteRepoViewModel



	//static fields
	companion object {
		private const val TAG = "mylogs_ChatFragment"

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

		remoteRepoViewModel = ViewModelProvider(this, factory)[RemoteRepoViewModel::class.java]

		sharedViewModel.getCurrentUser().observeOnce(this, Observer {
			userItemModel = it
			mChatAdapter.setCurrentUserId(it.baseUserInfo.userId)
		})

		//if it was a deep link navigation then create ConversationItem "on a flight"
		if (isDeepLinkJump) {
			val receivedConversationItem =
				ConversationItem(partner = BaseUserInfo(city = receivedPartnerCity,
				                                        gender = receivedPartnerGender,
				                                        userId = receivedPartnerId),
				                 conversationId = receivedConversationId,
				                 conversationStarted = true)

			sharedViewModel.conversationSelected.value = receivedConversationItem
		}

		remoteRepoViewModel.retrievedUserItem.observeOnce(this, Observer {
			currentPartner = it
			setupContentToolbar(it)
			sharedViewModel.userSelected.value = it
		})

		sharedViewModel.conversationSelected.observeOnce(this, Observer {
			currentConversation = it
			remoteRepoViewModel.getFullUserInfo(it.partner)
			associatedViewModel.loadMessages(it)
			associatedViewModel.observeNewMessages(it)
		})


		associatedViewModel.getMessagesList().observe(this, Observer {
			mChatAdapter.updateData(it)
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

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				btnSendMessage.isActivated = edTextMessageInput.text.isNotEmpty() &&
				                             edTextMessageInput.text.toString().trim().isNotEmpty()
			}
		})

		btnSendMessage.setOnClickListener { sendMessageClick() }

		//show attachment dialog picker
		btnSendAttachment.setOnClickListener {
			//show attachment dialog picker
			val materialDialogPicker = MaterialAlertDialogBuilder(it.context)
				.setItems(arrayOf("Camera", "Gallery")) {
					_, itemIndex ->
					if (itemIndex == 0) { photoCameraClick() }
					else { photoGalleryClick() }
				}
				.create()
			val params = materialDialogPicker.window?.attributes
			params?.gravity = Gravity.BOTTOM
			materialDialogPicker.show()
		}

		//if message contains photo then it opens in fullscreen dialog
		mChatAdapter.setOnAttachedPhotoClickListener(object: ChatAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {

				val photoUrl = mChatAdapter.getItem(position).photoItem!!.fileUrl
				val dialog = FullScreenDialogFragment.newInstance(photoUrl)
				dialog.show(childFragmentManager, FullScreenDialogFragment::class.java.canonicalName)

			}
		})

		val linearLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
		rvMessageList.apply {
			adapter = mChatAdapter
			layoutManager = linearLayoutManager
			//touch event guarantee that if user want to scroll or touches recycler with messages
			//keyboard hide and edittext focus clear
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
						associatedViewModel.loadMessages(currentConversation)
					}

				}
			})
		}

		toolbarChat.setNavigationOnClickListener { findNavController().navigateUp() }

		toolbarChat.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.chat_action_user -> {
					findNavController().navigate(R.id.action_chat_to_profileFragment)
				}

				R.id.chat_action_report -> {
					Toast.makeText(context, "chat report click", Toast.LENGTH_SHORT).show()
					//chatViewModel.loadMessages(currentConversation)
				}
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
			GlideApp.with(this)
				.load(partnerUserItem.photoURLs[0].fileUrl)
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
		if (edTextMessageInput.text.isNotEmpty() &&
		    edTextMessageInput.text.toString().trim().isNotEmpty()) {

			val message = MessageItem(sender = userItemModel.baseUserInfo,
			                          recipientId = currentConversation.partner.userId,
			                          text = edTextMessageInput.text.toString().trim(),
			                          photoItem = null,
			                          conversationId = currentConversation.conversationId)

			associatedViewModel.sendMessage(message)
			edTextMessageInput.setText("")
			rvMessageList.scrollToPosition(0)

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
					/** show message that permission is denied**/
					/** show message that permission is denied**/

					//snackbarWithoutAction(it.deniedMessageId)
				})
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// send photo from gallery
		if (requestCode == IMAGE_GALLERY_REQUEST) {
			if (resultCode == RESULT_OK) {

				val selectedUri = data?.data
				associatedViewModel.sendPhoto(selectedUri.toString(),
				                        userItemModel.baseUserInfo,
				                        currentConversation.partner.userId)
			}
		}
		// send photo taken by camera
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					associatedViewModel.sendPhoto(Uri.fromFile(mFilePathImageCamera).toString(),
					                        userItemModel.baseUserInfo,
					                        currentConversation.partner.userId)
				}
				else Toast.makeText(context,
						"filePathImageCamera is null or filePathImageCamera isn't exists",
						Toast.LENGTH_LONG)
						.show()
			}
		}
	}


	override fun onBackPressed() {
		findNavController().navigateUp()
	}


}
