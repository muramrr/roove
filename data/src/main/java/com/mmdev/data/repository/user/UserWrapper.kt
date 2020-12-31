/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.repository.user

import com.mmdev.business.local.LocalUserRepository
import com.mmdev.business.user.UserItem
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

	fun clearData() = localRepo.clear()

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
		localRepo.saveUserInfo(user)
		this.user = user
	}
}