package com.mmdev.domain.messages.model

//import com.google.firebase.firestore.ServerTimestamp

/* Created by A on 06.06.2019.*/

/* store chatmodel data class
    empty constructor needed for firebase */



data class Message (var senderUser: Sender? = null, val text: String = "",
                    var file: File? = null){

	//@ServerTimestamp val timestamp: Date? = null

}
