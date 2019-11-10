package com.mmdev.business.user.repository

import com.mmdev.business.user.model.UserItem
import io.reactivex.Completable
import io.reactivex.Single

/* Created by A on 29.09.2019.*/

/**
 * This is the documentation block about the class
 */
class UserRepository {

	interface LocalUserRepository {

		fun getSavedUser(): UserItem

		fun saveUserInfo(currentUserItem: UserItem)
	}

	interface RemoteUserRepository {

		fun createUserOnRemote(): Completable

		fun deleteUser(userId: String): Completable

		fun getUserById(userId: String): Single<UserItem>

	}


}
