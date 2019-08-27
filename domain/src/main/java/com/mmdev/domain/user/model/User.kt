package com.mmdev.domain.user.model

import java.util.*

data class User (val name: String = "",
                 val city: String = "",
                 val gender: String = "",
                 val preferedGender: String = "",
                 val mainPhotoUrl: String = "",
                 var photoURLs: ArrayList<String>? = null,
                 val userId: String = "")



