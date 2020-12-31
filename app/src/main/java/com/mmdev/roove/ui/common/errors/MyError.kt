/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 31.12.20 16:29
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.roove.ui.common.errors

/**
 * Custom class that holds error, which probably comes from data request
 */

data class MyError(private val errorType: ErrorType, private val error: Throwable) {

	private val errorText: String = "${errorType.name} ERROR: ${error.message}"

	fun getErrorMessage() = errorText

}