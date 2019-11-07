package com.mmdev.business.user.model

data class UserItem (val name: String = "",
                     //var age: Int = 0,
                     var city: String = "",
                     var gender: String = "",
                     var preferedGender: String = "",
                     val mainPhotoUrl: String = "",
                     var photoURLs: List<String>? = null,
                     val userId: String = ""){

    override fun toString(): String {
        return "UserItem{\n\tname=$name,\n\tid=$userId,\n\tPhotoUrl='$mainPhotoUrl\n}"
    }

}




