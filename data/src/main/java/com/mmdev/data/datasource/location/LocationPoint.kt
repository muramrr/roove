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

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator


/**
 * Wrapper for two coordinates (latitude and longitude)
 */
data class LocationPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
): Parcelable {
    
    constructor(parcel: Parcel): this(parcel.readDouble(), parcel.readDouble())
    
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }
    
    override fun describeContents(): Int {
        return 0
    }
    
    companion object CREATOR: Creator<LocationPoint> {
        override fun createFromParcel(parcel: Parcel): LocationPoint {
            return LocationPoint(parcel)
        }
        
        override fun newArray(size: Int): Array<LocationPoint?> {
            return arrayOfNulls(size)
        }
    }
    
}
