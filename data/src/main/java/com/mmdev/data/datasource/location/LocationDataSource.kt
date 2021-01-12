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
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.log.logDebug
import com.mmdev.data.core.log.logWarn
import com.mmdev.data.core.roundTo
import com.mmdev.domain.user.data.LocationPoint
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
import java.security.NoSuchProviderException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Utility class for easy access to the device location on Android
 */

@Singleton
class LocationDataSource @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val TAG = "mylogs_LocationSource"
    
        /** The internal name of the provider for the coarse location  */
        private const val PROVIDER_COARSE = LocationManager.NETWORK_PROVIDER
        /** The internal name of the provider for the fine location  */
        private const val PROVIDER_FINE = LocationManager.GPS_PROVIDER
        private const val PROVIDER_PASSIVE = LocationManager.PASSIVE_PROVIDER
    }
    private val providers = arrayOf(PROVIDER_COARSE, PROVIDER_FINE, PROVIDER_PASSIVE)
    
    private val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    fun locationSingle(): Single<LocationPoint> = SingleCreate<LocationPoint> { emitter ->
    
        val locationListener = LocationListener { location ->
            logWarn(TAG, "Location listener attached")
            val lat = location.latitude.roundTo(6)
            val lon = location.longitude.roundTo(6)
            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lon))
            emitter.onSuccess(LocationPoint(lat, lon, hash))
            logDebug(TAG, "Location emitted from listener")
        }
        
        val availableProviders = providers.filter { mLocationManager.isProviderEnabled(it) }
        
        logDebug(TAG, "available = $availableProviders")
        
        
        
        if (availableProviders.isNullOrEmpty()){
            emitter.onError(NoSuchProviderException())
        }
        else {
            if (ActivityCompat.checkSelfPermission(
                        context, permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context, permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
        
                emitter.onError(NoSuchProviderException())
            }
            else {
                
                availableProviders.forEach { availableProvider ->
                    
                    if (mLocationManager.getLastKnownLocation(availableProvider) != null) {
                        logDebug(TAG, "location from cache available for $availableProvider")
                        
                        val lastLocation = mLocationManager.getLastKnownLocation(availableProvider)!!
                        
                        //check if location is old
                        if (System.currentTimeMillis() - lastLocation.time > TimeUnit.HOURS.toMillis(1)) {
                            val lat = lastLocation.latitude.roundTo(6)
                            val lon = lastLocation.longitude.roundTo(6)
                            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lon))
                            emitter.onSuccess(LocationPoint(lat, lon, hash))
                            logDebug(TAG, "Location emitted from $availableProvider cache")
                           
                        }
                        
                        else {
                            mLocationManager.requestLocationUpdates(
                                availableProvider,
                                0,
                                0f,
                                locationListener
                            )
                        }
                      
                    }
                    else {
                        logDebug(TAG, "last location is null, listener attached for $availableProvider")
                        
                        mLocationManager.requestLocationUpdates(
                            availableProvider,
                            0,
                            0f,
                            locationListener
                        )
                    }
                }
            }
            
        }
        
        emitter.setCancellable {
            logWarn(TAG, "Location listener detached")
            mLocationManager.removeUpdates(locationListener)
        }
    }.subscribeOn(MySchedulers.trampoline()).timeout(30, SECONDS)
    
}