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