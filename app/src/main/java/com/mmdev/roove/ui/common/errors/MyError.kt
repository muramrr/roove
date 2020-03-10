/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.03.20 15:57
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.errors

/**
 * This is the documentation block about the class
 */

data class MyError (private val errorType: ErrorType, private val error: Throwable) {

	private val errorText: String = "${errorType.name} ERROR: ${error.message}"

	fun getErrorMessage()= errorText

}