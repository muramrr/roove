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

package com.mmdev.domain.user.data

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Wrapper for two coordinates (latitude and longitude)
 * Also contains method to retrieve bounds for given radius and using this points as center
 */

data class LocationPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    
    companion object {
        private const val EARTH_RADIUS = 6371.01
        private const val MIN_LAT = -Math.PI/2 // -90 degrees as radians
        private const val MAX_LAT = Math.PI / 2 // 90 degrees as radians
        private const val MIN_LON = -Math.PI // -180 degrees as radians
        private const val MAX_LON = Math.PI // 180 degrees as radians
        
    }
    
    private val radLat = Math.toRadians(latitude)
    private val radLon = Math.toRadians(longitude)
    
    /**
     * Computes the bounding coordinates of all points on the surface
     * of a sphere that have a great circle distance to the point represented
     * by this GeoLocation instance that is less or equal to the distance
     * argument.
     *
     * For more information about the formula used in this method visit
     * http://JanMatuschek.de/LatitudeLongitudeBoundingCoordinates.
     *
     * @param distanceRadius the distance from the point represented by this
     * GeoLocation instance. Must me measured in the same unit as the radius
     * argument.
     */
    fun getBounds(distanceRadius: Double): LocationBounds {
        val correctedDistanceRadius: Double = when {
            distanceRadius > EARTH_RADIUS -> EARTH_RADIUS
            distanceRadius <= 1.0 -> 1.0
            else -> distanceRadius
        }
        
        // angular distance in radians on a great circle
        val radDist = correctedDistanceRadius / EARTH_RADIUS
        var minLat: Double = radLat - radDist
        var maxLat: Double = radLat + radDist
        
        var minLon: Double
        var maxLon: Double
        if (minLat > MIN_LAT && maxLat < MAX_LAT) {
            val deltaLon = asin(sin(radDist) / cos(radLat))
            minLon = radLon - deltaLon
            if (minLon < MIN_LON) minLon += 2.0 * Math.PI
            maxLon = radLon + deltaLon
            if (maxLon > MAX_LON) maxLon -= 2.0 * Math.PI
        }
        else {
            // a pole is within the distance
            minLat = max(minLat, MIN_LAT)
            maxLat = min(maxLat, MAX_LAT)
            minLon = MIN_LON
            maxLon = MAX_LON
        }
        return LocationBounds(
            minPoint = LocationPoint(
                latitude = Math.toDegrees(minLat), longitude = Math.toDegrees(minLon)
            ), maxPoint = LocationPoint(
                latitude = Math.toDegrees(maxLat), longitude = Math.toDegrees(maxLon)
            )
        )
    }
    
    data class LocationBounds(
        val minPoint: LocationPoint, val maxPoint: LocationPoint
    )
}