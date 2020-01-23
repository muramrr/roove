/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 23.01.20 21:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.dating.chat.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mmdev.business.cards.CardItem
import com.mmdev.business.chat.entity.MessageItem
import com.mmdev.business.conversations.ConversationItem
import com.mmdev.business.user.UserItem
import com.mmdev.roove.BuildConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.databinding.FragmentChatBinding
import com.mmdev.roove.ui.core.BaseFragment
import com.mmdev.roove.ui.core.SharedViewModel
import com.mmdev.roove.ui.dating.chat.ChatViewModel
import com.mmdev.roove.utils.addSystemBottomPadding
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * This is the documentation block about the class
 */

class ChatFragment : BaseFragment(R.layout.fragment_chat) {

	private lateinit var userItemModel: UserItem


	//saving state
	private var partnerName = ""
	private var partnerMainPhotoUrl = ""
	private lateinit var currentConversation: ConversationItem
	private var isOnCreateCalled: Boolean = false


	private val mChatAdapter: ChatAdapter = ChatAdapter(listOf())

	// File
	private lateinit var mFilePathImageCamera: File

	private lateinit var sharedViewModel: SharedViewModel
	private lateinit var chatViewModel: ChatViewModel


	//static fields
	companion object {
		private const val IMAGE_GALLERY_REQUEST = 1
		private const val IMAGE_CAMERA_REQUEST = 2

		// Gallery Permissions
		private const val REQUEST_STORAGE = 1
		private val PERMISSIONS_STORAGE =
			arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

		// Camera Permission
		private const val REQUEST_CAMERA = 2
		private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		chatViewModel = ViewModelProvider(this@ChatFragment, factory)[ChatViewModel::class.java]

		sharedViewModel = activity?.run {
			ViewModelProvider(this, factory)[SharedViewModel::class.java]
		} ?: throw Exception("Invalid Activity")


		sharedViewModel.getCurrentUser().observe(this, Observer {
			userItemModel = it
			mChatAdapter.setCurrentUserId(it.baseUserInfo.userId)
		})

		sharedViewModel.conversationSelected.observe(this, Observer {
			if (!isOnCreateCalled && !this::currentConversation.isInitialized) {
				currentConversation = it
				partnerName = it.partner.name
				partnerMainPhotoUrl = it.partner.mainPhotoUrl
				setupContentToolbar()
				isOnCreateCalled = true
//				if (it.conversationId.isNotEmpty()) {
//					Log.wtf("mylogs_ChatFragment", "not empty")
//					chatViewModel.loadMessages(it)
//					chatViewModel.getMessagesList().observe(this, Observer { messageList ->
//						mChatAdapter.updateData(messageList)
//					})
//				}
//				else {
//					Log.wtf("mylogs_ChatFragment", "start listen to empty chat")
//					chatViewModel.startListenToEmptyChat(it.partner.userId)
//					chatViewModel.getMessagesList().observe(this, Observer { messageList ->
//						mChatAdapter.updateData(messageList)
//					})
//				}
			}
		})



	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentChatBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ChatFragment
				viewModel = chatViewModel
				executePendingBindings()
			}
			.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		containerChat.addSystemBottomPadding()

		edTextMessageInput.addTextChangedListener(object: TextWatcher{
			override fun afterTextChanged(s: Editable?) {}

			override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

			override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
				btnSendMessage.isActivated = edTextMessageInput.text.isNotEmpty() &&
				                             edTextMessageInput.text.toString().trim().isNotEmpty()
			}
		})

		btnSendMessage.setOnClickListener { sendMessageClick() }
		btnSendAttachment.setOnClickListener {
			val builder = AlertDialog.Builder(context!!)
				.setItems(arrayOf("Camera", "Gallery")) {
					_, which ->
					if (which == 0) { photoCameraClick() }
					else { photoGalleryClick() }
				}
			val alertDialog = builder.create()
			val params = alertDialog.window?.attributes
			params?.gravity = Gravity.BOTTOM
			alertDialog.show()
		}

		mChatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
			override fun onChanged() {
				super.onChanged()
				val friendlyMessageCount = mChatAdapter.itemCount
				rvMessageList.scrollToPosition(friendlyMessageCount - 1)
			}
		})

		mChatAdapter.setOnAttachedPhotoClickListener(object: ChatAdapter.OnItemClickListener {
			override fun onItemClick(view: View, position: Int) {

				val photoUrl = mChatAdapter.getItem(position).photoAttachementItem!!.fileUrl
				val dialog =
					FullScreenDialogFragment.newInstance(
							photoUrl)
				dialog.show(childFragmentManager,
				            FullScreenDialogFragment::class.java.canonicalName)

			}
		})

		rvMessageList.apply {
			adapter = mChatAdapter
			layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
		}

		toolbarChat.setNavigationOnClickListener { findNavController().navigateUp() }
		toolbarChat.setOnMenuItemClickListener { item ->
			when (item.itemId) {
				R.id.chat_action_user ->{
					findNavController().navigate(R.id.action_chat_to_profileFragment)
					sharedViewModel.setCardSelected(
							CardItem(currentConversation.partner,
							         currentConversation.conversationStarted)
					)
				}

				R.id.chat_action_report -> { Toast.makeText(context,
				                                            "chat report click",
				                                            Toast.LENGTH_SHORT).show()
				}
			}
			return@setOnMenuItemClickListener true
		}
	}

	override fun onResume() {
		// returns to fragment from onStop
		// onCreate is no longer being called in this scenario
		super.onResume()

		if (isOnCreateCalled && this::currentConversation.isInitialized) {
			partnerName = currentConversation.partner.name
			partnerMainPhotoUrl = currentConversation.partner.mainPhotoUrl

			if (currentConversation.conversationId.isNotEmpty()) {
				chatViewModel.loadMessages(currentConversation)
				chatViewModel.getMessagesList().observe(this, Observer { messageList ->
					mChatAdapter.updateData(messageList)
				})
			}
			else {
				chatViewModel.startListenToEmptyChat(currentConversation.partner.userId)
				chatViewModel.getMessagesList().observe(this, Observer { messageList ->
					mChatAdapter.updateData(messageList)
				})
			}
			setupContentToolbar()

		}
	}

	private fun setupContentToolbar(){
		toolbarChat.apply {
			//menu declared directly in xml
			//no need to inflate menu manually
			//set only title, actions and icon

			val partnerIcon = menu.findItem(R.id.chat_action_user)
			GlideApp.with(this)
				.load(partnerMainPhotoUrl)
				.centerCrop()
				.apply(RequestOptions().circleCrop())
				.into(object : CustomTarget<Drawable>(){
					override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
						partnerIcon.icon = resource
					}
					override fun onLoadCleared(placeholder: Drawable?) {}
				})

			title = partnerName
		}
	}

	/*
	* Send plain text msg to chat if editText is not empty
	* else shake animation
	* rx chain: create conversation -> set conversation id -> send message -> observe new messages
	*/
	private fun sendMessageClick() {
		if (edTextMessageInput.text.isNotEmpty() &&
		    edTextMessageInput.text.toString().trim().isNotEmpty()) {

			val message = MessageItem(userItemModel.baseUserInfo,
			                          edTextMessageInput.text.toString().trim(),
			                          photoAttachementItem = null)

			chatViewModel.sendMessage(message)
			edTextMessageInput.setText("")

		}
		else edTextMessageInput.startAnimation(
				AnimationUtils.loadAnimation(context, R.anim.horizontal_shake))

	}

	/*
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 */
	private fun photoCameraClick() {
		// Check if we have needed permissions
		val listPermissionsNeeded = ArrayList<String>()
		for (permission in PERMISSIONS_CAMERA) {
			val result = ActivityCompat.checkSelfPermission(context!!, permission)
			if (result != PackageManager.PERMISSION_GRANTED) listPermissionsNeeded.add(permission)
		}
		if (listPermissionsNeeded.isNotEmpty()) requestPermissions(PERMISSIONS_CAMERA,
		                                                           REQUEST_CAMERA)
		else startCameraIntent()
	}

	/*
	 * take photo directly by camera
	 */
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
		startActivityForResult(intent,
		                       IMAGE_CAMERA_REQUEST)
	}

	/*
	 * Checks if the app has permissions to READ user files
	 * If the app does not has permission then the user will be prompted to grant permissions
	 */
	private fun photoGalleryClick() {
		if (ActivityCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED)
			requestPermissions(PERMISSIONS_STORAGE,
			                   REQUEST_STORAGE)
		else startGalleryIntent()
	}

	/*
	 * choose photo from gallery
	 */
	private fun startGalleryIntent() {
		val intent = Intent().apply {
			action = Intent.ACTION_GET_CONTENT
			type = "image/*"
		}
		startActivityForResult(Intent.createChooser(intent, "Select image"),
		                       IMAGE_GALLERY_REQUEST)
	}

	// start after permissions was granted
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		// If request is cancelled, the result arrays are empty.
		if (requestCode == REQUEST_CAMERA)
		// check camera permission was granted
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				startCameraIntent()
		if (requestCode == REQUEST_STORAGE)
		// check gallery permission was granted
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				startGalleryIntent()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		// send photo from gallery
		if (requestCode == IMAGE_GALLERY_REQUEST) {
			if (resultCode == RESULT_OK) {

				val selectedUri = data?.data
				chatViewModel.sendPhoto(selectedUri.toString(),
				                        sender = userItemModel.baseUserInfo)


			}
		}
		// send photo taken by camera
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					chatViewModel.sendPhoto(Uri.fromFile(mFilePathImageCamera).toString(),
					                        sender = userItemModel.baseUserInfo)
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
