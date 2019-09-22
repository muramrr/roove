package com.mmdev.meetapp.ui.chat.view

import android.view.View

interface ClickChatAttachmentFirebase {

	/**
	 * click attached photo in chat
	 * @param view your view
	 * @param position pos
	 * @param nameUser sender name
	 * @param urlPhotoUser photo profile url sender
	 * @param urlPhotoClick clicked photo in chat url
	 */
	fun clickImageChat(view: View, position: Int, nameUser: String, urlPhotoUser: String, urlPhotoClick: String)

}
