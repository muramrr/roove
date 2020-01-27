/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 27.01.20 18:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FirestoreNotificationsService: FirebaseMessagingService() {


	override fun onMessageReceived(remoteMessage: RemoteMessage) {
		if (remoteMessage.notification != null)
			Log.wtf("mylogs", remoteMessage.data.toString())

//		val messageTitle = remoteMessage.notification!!.title
//		val messageBody = remoteMessage.notification!!.body
//		val clickAction = remoteMessage.notification!!.clickAction
//		val messageData = remoteMessage.data["message"]
//		val fromData = remoteMessage.data["from_id"]
//		// Create an explicit intent for an Activity in your app
//		val resultIntent = Intent(clickAction)
//		resultIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//		resultIntent.putExtra("message", messageData)
//		resultIntent.putExtra("from_id", fromData)
//		val pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0)
//		val mBuilder = NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
//			.setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(messageTitle)
//			.setContentText(messageBody).setStyle(NotificationCompat.BigTextStyle().bigText(messageTitle))
//			.setPriority(NotificationCompat.PRIORITY_MAX)
//		mBuilder.setContentIntent(pendingIntent)
//		val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(this)
//		val notificationId = System.currentTimeMillis().toInt()
//		// notificationId is a unique int for each notification that you must define
//		notificationManager.notify(notificationId, mBuilder.build())
	}


}
