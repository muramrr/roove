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

import android.content.Context
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.mmdev.data.core.MySchedulers
import com.mmdev.data.core.log.logWarn
import com.mmdev.data.core.log.logWtf
import com.mmdev.domain.user.data.LocationPoint
import im.delight.android.location.SimpleLocation
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.operators.single.SingleCreate
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
    }
    
    //private val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    fun locationSubject(): Single<LocationPoint> = SingleCreate<LocationPoint> { emitter ->
    
        //val locationListener = LocationListener { location ->
        //    logWarn(TAG, "Location listener attached")
        //    val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(location.latitude, location.longitude))
        //    emitter.onSuccess(LocationPoint(location.latitude, location.longitude, hash))
        //    logDebug(TAG, "Location emitted from listener")
        //}
        
        //val bestProvider = mLocationManager.getBestProvider(Criteria(), true)
        
        //logDebug(TAG, "best provider = $bestProvider")
    
        val location = SimpleLocation(context).apply {
            setListener {
                logWtf(TAG, "location changed: $latitude, $longitude")
                val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
                emitter.onSuccess(LocationPoint(latitude, longitude, hash))
            }
        }
        location.beginUpdates()
        
        //if (bestProvider.isNullOrBlank()){
        //    emitter.onError(NoSuchProviderException())
        //}
        //else {
        //
        //        mLocationManager.getLastKnownLocation(bestProvider)?.let {
        //            val hash = GeoFireUtils.getGeoHashForLocation(GeoLocation(it.latitude, it.longitude))
        //            emitter.onSuccess(LocationPoint(it.latitude, it.longitude, hash))
        //            logDebug(TAG, "Location emitted from cache")
        //        } ?: mLocationManager.requestLocationUpdates(
        //            bestProvider,
        //            0,
        //            0f,
        //            locationListener
        //        )
        //
        //
        //}
        
        emitter.setCancellable {
            logWarn(TAG, "Location listener detached")
            location.endUpdates()
        }
    }.subscribeOn(MySchedulers.trampoline())
    
}