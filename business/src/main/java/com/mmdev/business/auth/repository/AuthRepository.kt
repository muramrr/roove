/*
 * Created by Andrii Kovalchuk on 09.09.19 11:21
 * Copyright (c) 2019. All rights reserved.
 * Last modified 28.10.19 18:58
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.business.auth.repository

import com.mmdev.business.user.model.UserItem
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface AuthRepository {

	fun isAuthenticated(): Observable<Boolean>

	fun handleUserExistence(userId: String): Single<UserItem>

	fun signInWithFacebook(token: String): Single<UserItem>

	fun registerUser(userItem: UserItem): Completable

	fun logOut()

}