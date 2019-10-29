package com.mmdev.business.conversations.model

/* Created by A on 26.10.2019.*/

/**
 * This is the documentation block about the class
 */

data class ConversationItem(val conversationId: String = "",
                            val partnerId: String = "",
                            val partnerName: String = "",
                            val partnerPhotoUrl: String = "",
                            val lastMessageText: String = "")