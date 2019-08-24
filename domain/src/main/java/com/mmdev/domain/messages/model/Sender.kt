package com.mmdev.domain.messages.model

data class Sender (val name: String = "",
                   val mainPhotoUrl: String = "",
                   val gender: String = "",
                   val id: String = "") {

	override fun toString(): String {
		return "Sender{\n\tname=$name,\n\tid=$id,\n\tPhotoUrl='$mainPhotoUrl\n}"
	}

}