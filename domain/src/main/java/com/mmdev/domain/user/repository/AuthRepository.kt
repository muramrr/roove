package com.mmdev.domain.user.repository

import com.mmdev.domain.user.model.User
import io.reactivex.Single


interface AuthRepository {

    fun handleUserExistence(userId: String): Single<User>

    fun signup(user: User): Single<User>

}