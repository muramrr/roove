package com.mmdev.roove.ui.conversations.view

import com.mmdev.business.conversations.model.ConversationItem

/* Created by A on 27.10.2019.*/

/**
 * This is the documentation block about the class
 */

class ConversationsManager {


	companion object{
		fun generateConversationsList(): List<ConversationItem>{
			val conversationItemList = ArrayList<ConversationItem>()
			conversationItemList.add(ConversationItem("",
			                                          "Tvoi paren",
			                                          "",
			                                          "ты че сука"))
			conversationItemList.add(ConversationItem("",
			                                          "Майор миллиции",
			                                          "",
			                                          "priidi v uchastok"))
			conversationItemList.add(ConversationItem("",
			                                          "Мой парень",
			                                          "",
			                                          "привет любимая"))
			conversationItemList.add(ConversationItem("",
			                                          "Рандомный чел",
			                                          "",
			                                          "скинь сиськи"))
			conversationItemList.add(ConversationItem("",
			                                          "Турок",
			                                          "",
			                                          "дай писка лизат"))
			conversationItemList.add(ConversationItem("",
			                                          "Твой батя",
			                                          "",
			                                          "не еби мою дочь"))

			return conversationItemList
		}
	}


}