package com.mmdev.roove.utils.models

import java.util.*

data class ProfileModel (var name: String = "", var city: String = "", var gender: String = "",
                         var preferedGender: String = "",
                         var mainPhotoUrl: String = "",
                         var photoURLs: ArrayList<String>? = null, var userId: String = "")



