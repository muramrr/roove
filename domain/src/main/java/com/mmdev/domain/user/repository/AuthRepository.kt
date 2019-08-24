package com.mmdev.domain.user.repository

import com.mmdev.domain.user.model.User
import io.reactivex.Single


interface AuthRepository {

    fun signup(username: String, password: String): Single<User>

    fun login(username: String, password: String): Single<User>
}