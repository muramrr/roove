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
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.mmdev.domain.user.data.LocationPoint
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Utility class for easy access to the device location on Android
 */

@SuppressLint("MissingPermission")
@Singleton
class LocationDataSource @Inject constructor(context: Context): LocationListener {
    
    
    val locationSubject: BehaviorSubject<LocationPoint> = BehaviorSubject.create()
    
    /**
     * Creates a new LocationListener instance used internally to listen for location updates
     *
     * @return the new LocationListener instance
     */
    override fun onLocationChanged(location: Location) {
        val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(location.latitude, location.longitude))
        
        locationSubject.onNext(LocationPoint(location.latitude, location.longitude, hash))
        locationSubject.onComplete()
        endUpdates()
    }
    
    /** The LocationManager instance used to query the device location  */
    private val mLocationManager: LocationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager
    
    /** Starts updating the location and requesting new updates after the defined interval  */
    init {
        mLocationManager.getLastKnownLocation(getProviderName())?.let {
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(it.latitude, it.longitude))
            locationSubject.onNext(LocationPoint(it.latitude, it.longitude, hash))
        }
        mLocationManager.requestLocationUpdates(
            getProviderName(),
            0,
            0f,
            this
        )
    }
    
    companion object {
        private const val TAG = "mylogs_LocationSource"
    
        /** The internal name of the provider for the coarse location  */
        private const val PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER
    
        /** The internal name of the provider for the fine location  */
        private const val PROVIDER_FINE = LocationManager.GPS_PROVIDER
    
    }
    
    /** Stops the location updates when they aren't needed anymore so that battery can be saved  */
    fun endUpdates() = mLocationManager.removeUpdates(this)
    
    /**
     * Whether the device has location access enabled in the settings
     *
     * @return whether location access is enabled or not
     */
    fun hasLocationEnabled(): Boolean = hasLocationEnabled(getProviderName())
    
    private fun hasLocationEnabled(providerName: String): Boolean = try {
        mLocationManager.isProviderEnabled(providerName)
    } catch (e: Exception) {
        false
    }
    
    /**
     * Returns the name of the location provider that matches the specified settings
     * and depends on the given granularity
     *
     * @param requireFine whether to require fine location or use coarse location
     * @return the provider's name
     */
    private fun getProviderName(requireFine: Boolean = false): String {
        // if fine location (GPS) is required
        return if (requireFine) { PROVIDER_FINE }
        else { PROVIDER_COARSE }
    }
    
   
    
    
}