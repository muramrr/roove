package com.mmdev.meetapp.models

data class UserChatModel (var name: String? = "",
                          var mainPhotoUrl: String? = "",
                          var gender: String? = "",
                          var id: String? = "") {

	override fun toString(): String {
		return "UserChatModel{\n\tname=$name,\n\tid=$id,\n\tPhotoUrl='$mainPhotoUrl\n}"
	}

}