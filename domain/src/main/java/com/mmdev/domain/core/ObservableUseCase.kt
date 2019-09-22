package com.mmdev.domain.core

import io.reactivex.Observable


interface ObservableUseCase<T> {

    fun execute(): Observable<T>
}