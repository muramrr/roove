/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 16.03.20 14:38
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.user

import com.mmdev.business.core.UserItem
import com.mmdev.business.local.LocalUserRepository
import javax.inject.Inject
import javax.inject.Singleton


/**
 * wrapper current user to use as instance in singleton classes that requires actual user info
 * after it changed (user edit some info)
 */

@Singleton
class UserWrapper @Inject constructor(private val localRepo: LocalUserRepository) {

	//get preCached user obj
	private var user: UserItem = localRepo.getSavedUser()!!

	//this fun is used to avoid allocate unused copy of this.user made by getUser() fun
	//used to compare if user was changed
	fun getInMemoryUser() = user

	/**
	 * Problem: data can be modified outside the scope of the setter method,
	 * which breaks the encapsulation purpose of the setter
	 * @param this.user is assigned to the method’s parameter variable directly.
	 * That means both of the variables are referring to the same object in memory - userItem.
	 * So changes made to either the @this.user or @userWrapper.getUserInMemory()
	 * variables are actually made on the same object.
	 * A workaround for this situation is to copy object from the private this.user var to the
	 * new allocated object.
	 * @return conditional constant related to this class
	 */
	fun getUser(): UserItem = user.clone()
	fun setUser(user: UserItem) {
		this.user = user
		localRepo.saveUserInfo(user)
	}
}