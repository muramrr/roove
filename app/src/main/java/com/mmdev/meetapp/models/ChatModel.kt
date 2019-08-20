package com.mmdev.meetapp.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/* Created by A on 06.06.2019.*/

/* store chatmodel data class
    empty constructor needed for firebase */



data class ChatModel (var senderUserModel: UserChatModel? = null, var message: String? = "",
                      var fileModel: FileModel? = null){

	@ServerTimestamp val timestamp: Date? = null

}
