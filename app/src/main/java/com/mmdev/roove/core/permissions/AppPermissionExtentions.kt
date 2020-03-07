/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.03.20 13:54
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.core.permissions

import android.content.pm.PackageManager
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.mmdev.roove.core.permissions.AppPermission.Companion.REQUEST_CODE_CAMERA
import com.mmdev.roove.core.permissions.AppPermission.Companion.REQUEST_CODE_GALLERY


/**************************************
 * HANDLE PERMISSIONS IN FRAGMENTS *
 *************************************/

//check one single permission
fun Fragment.checkSinglePermission(permission: String) =
	(PermissionChecker.checkSelfPermission(this.context!!, permission) == PermissionChecker.PERMISSION_GRANTED)

//check if multiple permissions needed, but some of them was granted
fun Fragment.checkPermissionsNeeded(permissionsList: Array<String>): List<String> {
	val listPermissionsNeeded = ArrayList<String>()
	for (permission in permissionsList) {
		if (!checkSinglePermission(permission)) listPermissionsNeeded.add(permission)
	}
	return listPermissionsNeeded
}

//check if all of permissions was granted (list of needed permissions is empty)
fun Fragment.isPermissionsGranted(permission: AppPermission) = checkPermissionsNeeded(permission.permissionsList).isEmpty()

//fun Fragment.isExplanationNeeded(permission: AppPermission) =
	//shouldShowRequestPermissionRationale(permission.permissionsList)

fun Fragment.requestAppPermissions(permission: AppPermission) = requestPermissions(permission.permissionsList, permission.requestCode)

inline fun Fragment.handlePermission(permission: AppPermission,
                                     onGranted: (AppPermission) -> Unit,
                                     onDenied: (AppPermission) -> Unit,
                                     onExplanationNeeded: (AppPermission) -> Unit) {
	when {
		isPermissionsGranted(permission) -> onGranted(permission)
		//isExplanationNeeded(permission) -> onExplanationNeeded(permission)
		else -> onDenied(permission)
	}
}


fun onRequestPermissionsResultReceived(requestCode: Int, grantResults: IntArray,
                                       onPermissionGranted: (AppPermission) -> Unit,
                                       onPermissionDenied: (AppPermission) -> Unit) {

	when (requestCode) {

		REQUEST_CODE_CAMERA -> {
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				onPermissionGranted(AppPermission.CAMERA)
			else onPermissionDenied(AppPermission.CAMERA)
		}

		REQUEST_CODE_GALLERY -> {
			if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				onPermissionGranted(AppPermission.GALLERY)
			else onPermissionDenied(AppPermission.GALLERY)
		}

	}


}

