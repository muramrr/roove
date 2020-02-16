/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.02.20 15:36
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user

/**
 * This is the documentation block about the class
 */

data class BaseUserInfo(var name: String = "",
                        var age: Int = 0,
                        var city: String = "",
                        var gender: String = "",
                        var preferredGender: String = "",
                        var mainPhotoUrl: String = "",
                        var userId: String = "")