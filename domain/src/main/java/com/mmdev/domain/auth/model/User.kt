package com.mmdev.domain.auth.model

import java.util.*

data class User (val name: String = "",
                 var city: String = "",
                 var gender: String = "",
                 var preferedGender: String = "",
                 val mainPhotoUrl: String = "",
                 var photoURLs: ArrayList<String>? = null,
                 val userId: String = "")



