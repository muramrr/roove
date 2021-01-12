/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
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

package com.mmdev.data.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.data.core.firebase.getAndDeserializeAsSingle
import com.mmdev.data.core.firebase.setAsCompletable
import com.mmdev.data.core.firebase.updateAsCompletable
import com.mmdev.domain.user.data.UserItem

/**
 *
 */

class UserDataSource(private val fs: FirebaseFirestore) {
	
	private companion object {
		private const val FS_USERS_COLLECTION = "users"
	}
	
	fun getFirestoreUser(id: String) = fs.collection(FS_USERS_COLLECTION)
		.document(id)
		.getAndDeserializeAsSingle(UserItem::class.java)
	
	fun updateFirestoreUserField(
		id: String, field: String, value: Any
	) = fs.collection(FS_USERS_COLLECTION)
		.document(id)
		.updateAsCompletable(field, value)
	
	fun writeFirestoreUser(user: UserItem) = fs.collection(FS_USERS_COLLECTION)
		.document(user.baseUserInfo.userId)
		.setAsCompletable(user)
}