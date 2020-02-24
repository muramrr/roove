/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 24.02.20 16:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.core

import android.Manifest
import com.mmdev.roove.R

/**
 * This is the documentation block about the class
 */
sealed class AppPermission(val permissionsList: Array<String>,
                           val requestCode: Int,
                           val deniedMessageId: Int,
                           val explanationMessageId: Int) {
	companion object {
		const val REQUEST_CODE_CAMERA = 1
		const val REQUEST_CODE_GALLERY = 2
	}


	/**CAMERA PERMISSIONS**/
	object CAMERA : AppPermission(arrayOf(Manifest.permission.CAMERA,
	                                      Manifest.permission.READ_EXTERNAL_STORAGE),
	                              REQUEST_CODE_CAMERA,
	                              R.string.permission_camera_denied,
	                              R.string.permission_camera_explanation)

	/**READ/WRITE TO STORAGE PERMISSIONS**/
	object GALLERY : AppPermission(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
	                                       Manifest.permission.WRITE_EXTERNAL_STORAGE),
	                               REQUEST_CODE_GALLERY,
	                               R.string.permission_read_ext_storage_denied,
	                               R.string.permission_read_ext_storage_explanation)


}
