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

package com.mmdev.data.datasource.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.core.log.logWarn
import com.mmdev.domain.user.data.LocationPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.operators.observable.ObservableCreate
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Utility class for easy access to the device location on Android
 */

@SuppressLint("MissingPermission")
@Singleton
class LocationDataSource @Inject constructor(context: Context) {
    
    companion object {
        private const val TAG = "mylogs_LocationSource"
        
        /** The internal name of the provider for the coarse location */
        private const val PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER
        
        /** The internal name of the provider for the fine location */
        private const val PROVIDER_FINE = LocationManager.GPS_PROVIDER
        
    }
    /** The LocationManager instance used to query the device location */
    private val mLocationManager: LocationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager
    
    val locationSubject: Observable<LocationPoint> = ObservableCreate<LocationPoint> { emitter ->
        mLocationManager.getLastKnownLocation(getProviderName())?.let {
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(it.latitude, it.longitude))
            emitter.onNext(LocationPoint(it.latitude, it.longitude, hash))
            logDebug(TAG, "Location emitted")
        }
        val locationListener = LocationListener { location ->
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(location.latitude, location.longitude))
            emitter.onNext(LocationPoint(location.latitude, location.longitude, hash))
            logDebug(TAG, "Location emitted")
            emitter.onComplete()
        }
    
        logWarn(TAG, "Location listener attached")
        mLocationManager.requestLocationUpdates(
            getProviderName(),
            0,
            0f,
            locationListener
        )
        
        emitter.setCancellable {
            logWarn(TAG, "Location listener detached")
            endUpdates(locationListener)
        }
    }.subscribeOn(MySchedulers.trampoline())
    
    /** Stops the location updates when they aren't needed anymore so that battery can be saved */
    private fun endUpdates(locationListener: LocationListener) = mLocationManager.removeUpdates(locationListener)
    
    /**
     * Returns the name of the location provider that matches the specified settings
     * and depends on the given granularity
     *
     * @param requireFine whether to require fine location or use coarse location
     * @return the provider's name
     */
    private fun getProviderName(requireFine: Boolean = true): String {
        // if fine location (GPS) is required
        return if (requireFine) { PROVIDER_FINE }
        else { PROVIDER_COARSE }
    }
    
}