package com.mmdev.domain.messages.model

//import com.google.firebase.firestore.ServerTimestamp

/* Created by A on 06.06.2019.*/

/* store chatmodel data class
    empty constructor needed for firebase */



data class Message (val senderUser: Sender, val text: String = "",
                    val photoAttached: PhotoAttached?){

	//@ServerTimestamp val timestamp: Date? = null

}
