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

package com.mmdev.domain.user

import com.mmdev.domain.user.UserState.RegistrationStatus.REGISTERED
import com.mmdev.domain.user.UserState.RegistrationStatus.UNREGISTERED
import com.mmdev.domain.user.data.UserItem

/**
 * RxJava cannot emit a pure "null" so here is the wrapper
 */

sealed class UserState {
	data class AUTHENTICATED(val user: UserItem): UserState()
	object UNAUTHENTICATED: UserState()
	data class UNREGISTERED(val initialUserInfo: UserItem): UserState()
	
	inline fun <C> fold(
        authenticated: (UserItem) -> C,
        unauthenticated: () -> C,
        unregistered: (UserItem) -> C
	): C = when (this) {
		is AUTHENTICATED -> authenticated(user)
		is UNAUTHENTICATED -> unauthenticated()
		is UNREGISTERED -> unregistered(initialUserInfo)
	}
	
	companion object {
		val UNDEFINED = pack(null, UNREGISTERED)
		
		fun unregistered(user: UserItem) = pack(user, UNREGISTERED)
		fun registered(user: UserItem) = pack(user, REGISTERED)
		
		private fun pack(user: UserItem?, registration: RegistrationStatus) =
			if (user == null) UNAUTHENTICATED
			else when(registration) {
				REGISTERED -> AUTHENTICATED(user)
				UNREGISTERED -> UNREGISTERED(user)
			}
		
	}
	
	enum class RegistrationStatus {
		REGISTERED, UNREGISTERED
	}
}