/*
 * Created by Andrii Kovalchuk on
 * Copyright (c) 2019. All rights reserved.
 * Last modified 04.12.19 17:30
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.chat.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mmdev.business.chat.model.MessageItem
import com.mmdev.business.conversations.model.ConversationItem
import com.mmdev.business.user.model.UserItem
import com.mmdev.roove.BuildConfig
import com.mmdev.roove.R
import com.mmdev.roove.core.GlideApp
import com.mmdev.roove.core.injector
import com.mmdev.roove.databinding.FragmentChatBinding
import com.mmdev.roove.ui.MainActivity
import com.mmdev.roove.ui.chat.ChatViewModel
import com.mmdev.roove.utils.addSystemBottomPadding
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * This is the documentation block about the class
 */

class ChatFragment : Fragment(R.layout.fragment_chat) {


	private lateinit var  mMainActivity: MainActivity


	private val factory = injector.factory()
	private lateinit var chatViewModel: ChatViewModel


	// P O J O models
	private lateinit var userItemModel: UserItem

	private val mChatAdapter: ChatAdapter = ChatAdapter(listOf())

	// File
	private lateinit var mFilePathImageCamera: File

	private lateinit var conversationId: String


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

		//todo: remove data transfer between fragments, need to make it more abstract
		private const val CONVERSATION_KEY = "CONVERSATION_ID"
		@JvmStatic
		fun newInstance(conversationId: String) = ChatFragment().apply {
			arguments = Bundle().apply {
				putString(CONVERSATION_KEY, conversationId)
			}
		}

	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		activity?.let { mMainActivity = it as MainActivity }

		arguments?.let {
			conversationId = it.getString(CONVERSATION_KEY, "")
		}
		val conversation: ConversationItem? = mMainActivity.conversationItemClicked

		setHasOptionsMenu(true)

		userItemModel = mMainActivity.userItemModel
		mChatAdapter.setCurrentUserId(userItemModel.userId)

		chatViewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

		if (conversationId.isNotEmpty()) {
			chatViewModel.loadMessages(conversation!!)
			chatViewModel.getMessagesList().observe(this, Observer {
				mChatAdapter.updateData(it)
			})
		}
		else {
			chatViewModel.startListenToEmptyChat(mMainActivity.partnerId)
			chatViewModel.getMessagesList().observe(this, Observer {
				mChatAdapter.updateData(it)
			})
		}

	}


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?) =
		FragmentChatBinding.inflate(inflater, container, false)
			.apply {
				lifecycleOwner = this@ChatFragment
				viewModel = chatViewModel
			}
			.root

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		messageInputContainer.addSystemBottomPadding()
		buttonMessage.setOnClickListener { sendMessageClick() }
		buttonAttachments.setOnClickListener {
			val builder = AlertDialog.Builder(mMainActivity)
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
				chat_messages_rv.scrollToPosition(friendlyMessageCount-1)
			}
		})

		mChatAdapter.setOnAttachedPhotoClickListener(object: ChatAdapter.OnItemClickListener{
			override fun onItemClick(view: View, position: Int) {
				val photoUrl = mChatAdapter.getItem(position).photoAttachementItem!!.fileUrl
				val dialog = FullScreenDialogFragment.newInstance(photoUrl)
				dialog.show(childFragmentManager,
				            FullScreenDialogFragment::class.java.canonicalName)
			}
		})

		chat_messages_rv.apply {
			adapter = mChatAdapter
			layoutManager = LinearLayoutManager(this.context).apply {
				stackFromEnd = true
			}
		}

	}

	/*
	* Send plain text msg to chat if editText is not empty
	* else shake animation
	* rx chain: create conversation -> set conversation id -> send message -> observe new messages
	*/
	private fun sendMessageClick() {
		if (textMessageInput.text.isNotEmpty() &&
		    textMessageInput.text.toString().trim().isNotEmpty()) {

			val message = MessageItem(userItemModel,
			                          textMessageInput.text.toString().trim(),
			                          photoAttachementItem = null)

			chatViewModel.sendMessage(message)
			textMessageInput.setText("")

		}
		else textMessageInput.startAnimation(AnimationUtils.loadAnimation(mMainActivity,
		                                                                R.anim.horizontal_shake))

	}

	/*
	 * Checks if the app has permissions to OPEN CAMERA and take photos
	 * If the app does not has permission then the user will be prompted to grant permissions
	 */
	private fun photoCameraClick() {
		// Check if we have needed permissions
		val listPermissionsNeeded = ArrayList<String>()
		for (permission in PERMISSIONS_CAMERA) {
			val result = ActivityCompat.checkSelfPermission(mMainActivity, permission)
			if (result != PackageManager.PERMISSION_GRANTED) listPermissionsNeeded.add(permission)
		}
		if (listPermissionsNeeded.isNotEmpty()) requestPermissions(PERMISSIONS_CAMERA, REQUEST_CAMERA)
		else startCameraIntent()
	}

	/*
	 * take photo directly by camera
	 */
	private fun startCameraIntent() {
		val namePhoto = DateFormat.format("yyyy-MM-dd_hhmmss", Date()).toString()
		mFilePathImageCamera = File(mMainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
		                            namePhoto + "camera.jpg")
		val photoURI = FileProvider.getUriForFile(mMainActivity,
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
	 */
	private fun photoGalleryClick() {
		if (ActivityCompat.checkSelfPermission(mMainActivity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			requestPermissions(PERMISSIONS_STORAGE, REQUEST_STORAGE)
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
		startActivityForResult(Intent.createChooser(intent, "Select image"), IMAGE_GALLERY_REQUEST)
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
				chatViewModel.sendPhoto(selectedUri.toString(), userItemModel)

			}
		}
		// send photo taken by camera
		if (requestCode == IMAGE_CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {

				if (mFilePathImageCamera.exists()) {
					chatViewModel.sendPhoto(Uri.fromFile(mFilePathImageCamera).toString(),
					                        userItemModel)
				}
				else Toast.makeText(mMainActivity,
						"filePathImageCamera is null or filePathImageCamera isn't exists",
						Toast.LENGTH_LONG)
						.show()
			}
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.chat_menu, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		val partnerIcon = menu.findItem(R.id.action_user)
		GlideApp.with(this)
			.load(mMainActivity.partnerMainPhotoUrl)
			.centerCrop()
			.apply(RequestOptions().circleCrop())
			.into(object : CustomTarget<Drawable>(){
				override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
					partnerIcon.icon = resource
				}

				override fun onLoadCleared(placeholder: Drawable?) {}
			})
		super.onPrepareOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_user -> {
				mMainActivity.startProfileFragment(mMainActivity.partnerId, false)
			}
			R.id.action_report -> { mMainActivity.showToast("report clicked") }
		}
		return true
	}

//	override fun onResume() {
//		super.onResume()
//		mMainActivity.toolbar.title = mMainActivity.partnerName
//		mMainActivity.setNonScrollableToolbar()
//	}

}
