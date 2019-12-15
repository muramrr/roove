/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2019. All rights reserved.
 * Last modified 15.12.19 16:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user.entity

data class UserItem (var name: String = "",
                     var age: Int = 0,
                     var city: String = "",
                     var gender: String = "",
                     var preferredGender: String = "",
                     val mainPhotoUrl: String = "",
                     var photoURLs: List<String> = listOf(),
                     val userId: String = ""){

	override fun toString(): String {
		return "UserItem{\n\tname=$name,\n\tid=$userId,\n\tPhotoUrl='$mainPhotoUrl\n}"
	}

}




