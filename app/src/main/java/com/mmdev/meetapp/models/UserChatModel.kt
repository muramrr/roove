package com.mmdev.meetapp.models

data class UserChatModel (val name: String = "",
                          val mainPhotoUrl: String = "",
                          val gender: String = "",
                          val id: String = "") {

	override fun toString(): String {
		return "UserChatModel{\n\tname=$name,\n\tid=$id,\n\tPhotoUrl='$mainPhotoUrl\n}"
	}

}