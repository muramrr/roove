package com.mmdev.domain.auth.repository

import com.mmdev.domain.auth.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface AuthRepository {

	fun isAuthenticated(): Observable<Boolean>

	fun handleUserExistence(userId: String): Single<User>

	fun signInWithFacebook(token: String): Single<User>

	fun signUp(user: User): Completable

	fun logOut()

}