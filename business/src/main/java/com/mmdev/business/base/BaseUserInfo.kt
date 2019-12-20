/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 20.12.19 17:59
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.base

/**
 * This is the documentation block about the class
 */

data class BaseUserInfo(val name: String = "",
                        val age: Int = 0,
                        val city: String = "",
                        val gender: String = "",
                        val mainPhotoUrl: String = "",
                        val userId: String = "")