package com.mmdev.domain.core

import io.reactivex.Single


interface SingleWithParamUseCase<T1,T2> {

    fun execute(t: T1): Single<T2>

}