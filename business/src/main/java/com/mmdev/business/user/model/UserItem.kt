/*
 * Created by Andrii Kovalchuk on 27.09.19 04:15
 * Copyright (c) 2019. All rights reserved.
 * Last modified 12.11.19 20:49
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.user.model

data class UserItem (val name: String = "",
                     //var age: Int = 0,
                     var city: String = "",
                     var gender: String = "",
                     var preferedGender: String = "",
                     val mainPhotoUrl: String = "",
                     var photoURLs: List<String> = listOf(),
                     val userId: String = ""){

    override fun toString(): String {
        return "UserItem{\n\tname=$name,\n\tid=$userId,\n\tPhotoUrl='$mainPhotoUrl\n}"
    }

}




