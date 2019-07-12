package com.mmdev.meetups.ui.adapters;

import android.view.View;

public interface ClickChatAttachmentsFirebase
{

	/**
	 * click attached photo in chat
	 * @param view your view
	 * @param position pos
	 * @param nameUser sender name
	 * @param urlPhotoUser photo profile url sender
	 * @param urlPhotoClick clicked photo in chat url
	 */
	void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick);

	/**
	 * click attached geoposition in chat
	 * @param view your view
	 * @param position pos
	 * @param latitude latitude
	 * @param longitude longitude
	 */
	void clickMapChat (View view, int position, String latitude, String longitude);

}
