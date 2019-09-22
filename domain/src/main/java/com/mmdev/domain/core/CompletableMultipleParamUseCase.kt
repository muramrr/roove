package com.mmdev.domain.core

import io.reactivex.Completable

/* Created by A on 26.08.2019.*/

/**
 * This is the documentation block about the class
 */

interface CompletableMultipleParamUseCase<in T1, T2> {
	fun execute(t1: T1, t2: T2): Completable
}