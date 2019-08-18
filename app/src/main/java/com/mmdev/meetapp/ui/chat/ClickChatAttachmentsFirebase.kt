package com.mmdev.meetapp.ui.chat

import android.view.View

interface ClickChatAttachmentsFirebase {

	/**
	 * click attached photo in chat
	 * @param view your view
	 * @param position pos
	 * @param nameUser sender name
	 * @param urlPhotoUser photo profile url sender
	 * @param urlPhotoClick clicked photo in chat url
	 */
	fun clickImageChat(view: View, position: Int, nameUser: String, urlPhotoUser: String, urlPhotoClick: String)

	/**
	 * click attached geoposition in chat
	 * @param view your view
	 * @param position pos
	 * @param latitude latitude
	 * @param longitude longitude
	 */
	fun clickMapChat(view: View, position: Int, latitude: String, longitude: String)

}
