/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
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
fun Fragment.checkSinglePermission(permission: String) = PermissionChecker
	.checkSelfPermission(this.requireContext(), permission) == PermissionChecker.PERMISSION_GRANTED

//check if multiple permissions needed, but some of them was granted
fun Fragment.checkPermissionsNeeded(permissionsList: Array<String>): List<String> {
	val listPermissionsNeeded = ArrayList<String>()
	for (permission in permissionsList) {
		if (!checkSinglePermission(permission)) listPermissionsNeeded.add(permission)
	}
	return listPermissionsNeeded
}

//check if all of permissions was granted (list of needed permissions is empty)
fun Fragment.isPermissionsGranted(permission: AppPermission) =
	checkPermissionsNeeded(permission.permissionsList).isEmpty()

//fun Fragment.isExplanationNeeded(permission: AppPermission) =
	//shouldShowRequestPermissionRationale(permission.permissionsList)

fun Fragment.requestAppPermissions(permission: AppPermission) =
	requestPermissions(permission.permissionsList, permission.requestCode)

inline fun Fragment.handlePermission(
	permission: AppPermission,
	onGranted: (AppPermission) -> Unit,
	onDenied: (AppPermission) -> Unit,
	onExplanationNeeded: (AppPermission) -> Unit
) = when {
	isPermissionsGranted(permission) -> onGranted(permission)
	//isExplanationNeeded(permission) -> onExplanationNeeded(permission)
	else -> onDenied(permission)
}



inline fun onRequestPermissionsResultReceived(
	requestCode: Int,
	grantResults: IntArray,
	onPermissionGranted: (AppPermission) -> Unit,
	onPermissionDenied: (AppPermission) -> Unit
) = when (requestCode) {
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
	
	else -> {}
}
