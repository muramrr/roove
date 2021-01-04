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
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import com.mmdev.data.core.log.logWtf
import com.mmdev.domain.user.data.LocationPoint
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos


/**
 * Utility class for easy access to the device location on Android
 *
 * based on:
 * https://github.com/delight-im/Android-SimpleLocation/blob/master/Source/library/src/main/java/im/delight/android/location/SimpleLocation.java
 */

@SuppressLint("MissingPermission")
@Singleton
class LocationDataSource @Inject constructor(context: Context): LocationListener {
    
    
    val locationSubject: PublishSubject<LocationPoint> = PublishSubject.create()
    
    /**
     * Creates a new LocationListener instance used internally to listen for location updates
     *
     * @return the new LocationListener instance
     */
    override fun onLocationChanged(location: Location) {
        logWtf(TAG, "$location")
        locationSubject.onNext(
            LocationPoint(
            latitude = location.latitude,
            longitude = location.longitude
        )
        )
        endUpdates()
    }
    
    /** The LocationManager instance used to query the device location  */
    private val mLocationManager: LocationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager
    
    /** Starts updating the location and requesting new updates after the defined interval  */
    init {
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
        
        
        /** The factor for conversion from latitude to kilometers  */
        private const val LATITUDE_TO_KILOMETER = 111.133f
        
        /** The factor for conversion from longitude to kilometers at zero degree in latitude  */
        private const val LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE = 111.320f
        
        /**
         * Opens the device's settings screen where location access can be enabled
         *
         * @param context the Context reference to start the Intent from
         */
        fun openSettings(context: Context) = context.startActivity(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        )
        
        /**
         * Converts a difference in latitude to a difference in kilometers (rough estimation)
         *
         * @param latitude the latitude (difference)
         * @return the kilometers (difference)
         */
        fun latitudeToKilometer(latitude: Double): Double = latitude * LATITUDE_TO_KILOMETER
        
        /**
         * Converts a difference in kilometers to a difference in latitude (rough estimation)
         * @param kilometer the kilometers (difference)
         * @return the latitude (difference)
         */
        fun kilometerToLatitude(kilometer: Double): Double = kilometer / latitudeToKilometer(1.0)
        
        
        /**
         * Converts a difference in longitude to a difference in kilometers (rough estimation)
         *
         * @param longitude the longitude (difference)
         * @param latitude the latitude (absolute)
         * @return the kilometers (difference)
         */
        fun longitudeToKilometer(longitude: Double, latitude: Double): Double =
            longitude * LONGITUDE_TO_KILOMETER_AT_ZERO_LATITUDE * cos(Math.toRadians(latitude))
        
        /**
         * Converts a difference in kilometers to a difference in longitude (rough estimation)
         * @param kilometer the kilometers (difference)
         * @param latitude the latitude (absolute)
         * @return the longitude (difference)
         */
        fun kilometerToLongitude(kilometer: Double, latitude: Double): Double =
            kilometer / longitudeToKilometer(1.0, latitude)
    }
    
    /** Stops the location updates when they aren't needed anymore so that battery can be saved  */
    fun endUpdates() = mLocationManager.removeUpdates(this)
    
    /** The current location with latitude, longitude, speed and altitude  */
    private var mPosition: Location? = null
    
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
     * Returns the current position as a Point instance
     *
     * @return the current location (if any) or `null`
     */
    val locationPoint: LocationPoint =
        if (mPosition == null) LocationPoint()
        else LocationPoint(mPosition!!.latitude, mPosition!!.longitude)
    
   
    
    fun getLatitude(): Double = mPosition?.latitude ?: 0.0
    fun getLongitude(): Double = mPosition?.longitude ?: 0.0
    fun getTimestampInMilliseconds(): Long = mPosition?.time ?: 0
    fun getElapsedTimeInNanoseconds(): Long = mPosition?.elapsedRealtimeNanos ?: 0
    fun getAltitude(): Double = mPosition?.altitude ?: 0.0
    
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