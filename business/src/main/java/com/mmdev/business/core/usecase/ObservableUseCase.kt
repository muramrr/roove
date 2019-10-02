package com.mmdev.business.core.usecase

import io.reactivex.Observable


interface ObservableUseCase<T> {

    fun execute(): Observable<T>
}