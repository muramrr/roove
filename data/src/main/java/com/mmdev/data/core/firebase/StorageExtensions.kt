/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2021. roove
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.data.core.firebase

import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.core.log.logError
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate

/**
 *
 */

private const val TAG = "mylogs_FirebaseExtensions"

internal fun StorageReference.deleteAsCompletable(): Completable = CompletableCreate { emitter ->
	delete()
		.addOnSuccessListener {
			logDebug(TAG, "Delete file at $path successfully")
			emitter.onComplete()
		}
		.addOnFailureListener { exception ->
			if (exception is StorageException && exception.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND)
				emitter.onComplete()
			else {
				logError(TAG, "Delete file at $path with error: $exception")
				emitter.onError(exception)
			}
		}
	
}.subscribeOn(MySchedulers.io())