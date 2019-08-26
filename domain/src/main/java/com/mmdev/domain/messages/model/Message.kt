package com.mmdev.domain.messages.model
/* Created by A on 06.06.2019.*/

/* store chatmodel data class
    empty constructor needed for firebase */



data class Message (val senderUser: Sender, val text: String = "",
                    var photoAttached: PhotoAttached?){

    //val messageType: String = if (photoAttached != null) "photo" else "text"

}
