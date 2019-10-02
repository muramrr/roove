package com.mmdev.business.core.usecase

import io.reactivex.Single


interface SingleUseCase<T> {

    fun execute(): Single<T>
}