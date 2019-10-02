package com.mmdev.business.core.usecase

import io.reactivex.Completable


interface CompletableWithParamUseCase<in T> {

    fun execute(t: T): Completable
}