package com.mmdev.domain.user.repository

import com.mmdev.domain.core.model.User

/* Created by A on 29.09.2019.*/

/**
 * This is the documentation block about the class
 */

interface UserRepository {

	fun getSavedUser(): User

	fun saveUserInfo(currentUser: User)

}