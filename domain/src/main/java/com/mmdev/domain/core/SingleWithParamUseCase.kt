package com.mmdev.domain.core

import io.reactivex.Single


interface SingleWithParamUseCase<T> {

    fun execute(t: T): Single<T>

}