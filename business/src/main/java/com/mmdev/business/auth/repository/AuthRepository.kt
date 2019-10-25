package com.mmdev.business.auth.repository

import com.mmdev.business.user.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface AuthRepository {

	fun isAuthenticated(): Observable<Boolean>

	fun handleUserExistence(userId: String): Single<User>

	fun signInWithFacebook(token: String): Single<User>

	fun registerUser(user: User): Completable

	fun logOut()

}