/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.01.20 20:27
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.notifications

import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mmdev.roove.R
import com.mmdev.roove.ui.MainActivity


class FirestoreNotificationsService: FirebaseMessagingService() {


	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		Log.wtf("mylogs_SERVICENOTIFICATIONS", remoteMessage.data.toString())

		val conversation = bundleOf("CONVERSATION_ID" to remoteMessage.data["CONVERSATION_ID"])

		val pendingIntent = NavDeepLinkBuilder(this)
			.setComponentName(MainActivity::class.java)
			.setGraph(R.navigation.main_navigation)
			.setDestination(R.id.chatFragmentNav)
			.setArguments(conversation)
			.createPendingIntent()

		val notificationBuilder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
			.setSmallIcon(R.drawable.ic_arrow_drop_up_24dp)
			.setContentTitle(remoteMessage.data["SENDER_NAME"])
			.setContentText(remoteMessage.data["CONTENT"])
			.setCategory(NotificationCompat.CATEGORY_MESSAGE)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setContentIntent(pendingIntent)


		val notificationId = System.currentTimeMillis().toInt()
		// notificationId is a unique int for each notification that you must define
		NotificationManagerCompat.from(this).notify(notificationId, notificationBuilder.build())
	}


}
