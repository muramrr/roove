package com.mmdev.domain.core.model

import java.util.*

data class User (val name: String = "",
                 var city: String = "",
                 var gender: String = "",
                 var preferedGender: String = "",
                 val mainPhotoUrl: String = "",
                 var photoURLs: ArrayList<String>? = null,
                 val userId: String = ""){

    override fun toString(): String {
        return "Sender{\n\tname=$name,\n\tid=$userId,\n\tPhotoUrl='$mainPhotoUrl\n}"
    }

}




