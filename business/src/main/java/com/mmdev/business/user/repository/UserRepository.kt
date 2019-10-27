package com.mmdev.business.user.repository

import com.mmdev.business.user.model.UserItem

/* Created by A on 29.09.2019.*/

/**
 * This is the documentation block about the class
 */

interface UserRepository {

	fun getSavedUser(): UserItem

	fun saveUserInfo(currentUserItem: UserItem)

}