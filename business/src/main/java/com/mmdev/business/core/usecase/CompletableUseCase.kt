package com.mmdev.business.core.usecase

import io.reactivex.Completable


interface CompletableUseCase {

    fun execute(): Completable
}