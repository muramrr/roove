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

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.provider.Settings
import androidx.annotation.RequiresPermission
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.cos


/**
 * Utility class for easy access to the device location on Android
 *
 * based on:
 * https://github.com/delight-im/Android-SimpleLocation/blob/master/Source/library/src/main/java/im/delight/android/location/SimpleLocation.java
 */

@Singleton
class LocationDataSource @Inject constructor(
    context: Context
) {
    
    companion object {
        /** The internal name of the provider for the coarse location  */
        private const val PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER
        
        /** The internal name of the provider for the fine location  */
        private const val PROVIDER_FINE = LocationManager.GPS_PROVIDER
        
        /** The factor for conversion from kilometers to meters  */
        private const val KILOMETER_TO_METER = 1000.0f
        
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
            Intent(
                Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
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
         * Converts a difference in latitude to a difference in meters (rough estimation)
         *
         * @param latitude the latitude (difference)
         * @return the meters (difference)
         */
        fun latitudeToMeter(latitude: Double): Double = latitudeToKilometer(latitude) * KILOMETER_TO_METER
        
        /**
         * Converts a difference in meters to a difference in latitude (rough estimation)
         * @param meter the meters (difference)
         * @return the latitude (difference)
         */
        fun meterToLatitude(meter: Double): Double = meter / latitudeToMeter(1.0)
        
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
        
        /**
         * Converts a difference in longitude to a difference in meters (rough estimation)
         *
         * @param longitude the longitude (difference)
         * @param latitude the latitude (absolute)
         * @return the meters (difference)
         */
        fun longitudeToMeter(longitude: Double, latitude: Double): Double =
            longitudeToKilometer(longitude, latitude) * KILOMETER_TO_METER
        
        /**
         * Converts a difference in meters to a difference in longitude (rough estimation)
         * @param meter the meters (difference)
         * @param latitude the latitude (absolute)
         * @return the longitude (difference)
         */
        fun meterToLongitude(meter: Double, latitude: Double): Double =
            meter / longitudeToMeter(1.0, latitude)
        
        /**
         * Calculates the difference from the start position to the end position (in meters)
         *
         * @param start the start position
         * @param end the end position
         * @return the distance in meters
         */
        fun calculateDistance(start: LocationPoint, end: LocationPoint): Double {
            return calculateDistance(start.latitude, start.longitude, end.latitude, end.longitude)
        }
        
        /**
         * Calculates the difference from the start position to the end position (in meters)
         *
         * @param startLatitude the latitude of the start position
         * @param startLongitude the longitude of the start position
         * @param endLatitude the latitude of the end position
         * @param endLongitude the longitude of the end position
         * @return the distance in meters
         */
        fun calculateDistance(
            startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double
        ): Double {
            val results = FloatArray(3)
            Location.distanceBetween(
                startLatitude, startLongitude, endLatitude, endLongitude, results
            )
            return results[0].toDouble()
        }
    }
    
    /** The LocationManager instance used to query the device location  */
    private val mLocationManager: LocationManager = context.getSystemService(
        Context.LOCATION_SERVICE
    ) as LocationManager
    
    /** The current location with latitude, longitude, speed and altitude  */
    @SuppressLint("MissingPermission")
    private var mPosition: Location? = getCachedPosition()
    
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
    val position: LocationPoint? =
        if (mPosition == null) null
        else LocationPoint(mPosition!!.latitude, mPosition!!.longitude)
    
   
    
    /**
     * Returns the latitude of the current location
     *
     * @return the current latitude (if any) or `0`
     */
    fun getLatitude(): Double = mPosition?.latitude ?: 0.0
    
    /**
     * Returns the longitude of the current location
     *
     * @return the current longitude (if any) or `0`
     */
    fun getLongitude(): Double = mPosition?.longitude ?: 0.0
    
    /**
     * Returns the timestamp of the current location as a number of milliseconds since January 1, 1970 (UTC)
     *
     * @return the timestamp (if any) or `0`
     */
    fun getTimestampInMilliseconds(): Long = mPosition?.time ?: 0
    
    /**
     * Returns the elapsed time since system boot of the current location in nanoseconds
     *
     * @return the elapsed time (if any) or `0`
     */
    fun getElapsedTimeInNanoseconds(): Long = mPosition?.elapsedRealtimeNanos ?: 0
    
    /**
     * Returns the current altitude
     *
     * @return the current altitude (if detected) or `0`
     */
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
        return if (requireFine) {
            PROVIDER_FINE
            // we just have to decide between active and passive mode
//            if (mPassive) {
//                PROVIDER_FINE_PASSIVE
//            }
//            else {
//                PROVIDER_FINE
//            }
        }
        else {
            PROVIDER_COARSE
            // if we can use coarse location (network)
//            if (hasLocationEnabled(PROVIDER_COARSE)) {
//                PROVIDER_COARSE
//                // if we wanted passive mode
//                if (mPassive) {
//                    // throw an exception because this is not possible
//                    throw RuntimeException("There is no passive provider for the coarse location")
//                }
//                else {
//                    // use coarse location (network)
//                    PROVIDER_COARSE
//                }
//            }
//            else {
//                // if we can use fine location (GPS)
//                if (hasLocationEnabled(PROVIDER_FINE) || hasLocationEnabled(PROVIDER_FINE_PASSIVE)) {
//                    // we have to use fine location (GPS) because coarse location (network) was not available
//                    getProviderName(true)
//                }
//                else {
//                    PROVIDER_COARSE
//                }
//            }
        }
    }
    
    /**
     * Returns the last position from the cache
     *
     * @return the cached position
     */
    @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
    private fun getCachedPosition(): Location? = try {
        mLocationManager.getLastKnownLocation(getProviderName())
    } catch (e: Exception) {
        null
    }
    

}