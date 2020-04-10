/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.04.20 17:39
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.data.places

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.mmdev.business.places.BasePlaceInfo
import com.mmdev.business.places.PlacesResponse
import com.mmdev.business.places.repository.PlacesRepository
import com.mmdev.data.core.BaseRepositoryImpl
import com.mmdev.data.core.schedulers.ExecuteSchedulers
import com.mmdev.data.user.UserWrapper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.completable.CompletableCreate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This is the documentation block about the class
 */

@Singleton
class PlacesRepositoryImpl @Inject constructor(firestore: FirebaseFirestore,
                                               private val placesApi: PlacesApi,
                                               private val userWrapper: UserWrapper):
		PlacesRepository, BaseRepositoryImpl(firestore, userWrapper) {

	//current time
	private val unixTime = System.currentTimeMillis() / 1000L
	private var page = 1

	companion object {
		private const val USER_PLACES_LIST_FIELD = "placesToGo"
	}

	override fun addPlaceToWantToGoList(basePlaceInfo: BasePlaceInfo): Completable =
		CompletableCreate { emitter ->
			currentUserDocRef
				.update(USER_PLACES_LIST_FIELD, FieldValue.arrayUnion(basePlaceInfo))
				.addOnSuccessListener {
					currentUser.placesToGo.add(basePlaceInfo)
					userWrapper.setUser(currentUser)
					emitter.onComplete()
				}
				.addOnFailureListener { emitter.onError(it) }
		}

	override fun loadFirstPlaces(category: String): Single<PlacesResponse> {
		page = 0
		return placesApi.getPlacesList(unixTime, category, currentUser.baseUserInfo.city)
			.subscribeOn(ExecuteSchedulers.io())
	}

	override fun loadMorePlaces(category: String): Single<PlacesResponse> {
		page++
		return placesApi.getPlacesList(unixTime, category, currentUser.baseUserInfo.city, page)
			.onErrorReturn { PlacesResponse() }
			.subscribeOn(ExecuteSchedulers.io())
	}

	override fun getPlaceDetails(id: Int) = placesApi.getPlaceDetails(id)


	override fun removePlaceFromWantToGoList(basePlaceInfo: BasePlaceInfo): Completable =
		CompletableCreate { emitter ->
			currentUserDocRef
				.update(USER_PLACES_LIST_FIELD, FieldValue.arrayRemove(basePlaceInfo))
				.addOnSuccessListener {
					currentUser.placesToGo.remove(basePlaceInfo)
					userWrapper.setUser(currentUser)
					emitter.onComplete()
				}
				.addOnFailureListener { emitter.onError(it) }
		}

}