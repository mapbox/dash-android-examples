package com.mapbox.dash.showcase.app

import com.mapbox.common.location.Location
import com.mapbox.dash.sdk.coordination.PointDestination
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIUS_IN_DEGREES = 2000.0 / 111000.0

internal fun Location.getRandomDestinationAround(): PointDestination {
    val random = Random()
    val u: Double = random.nextDouble()
    val v: Double = random.nextDouble()
    val w = RADIUS_IN_DEGREES * sqrt(u)
    val t = 2 * Math.PI * v
    val x = w * cos(t)
    val y = w * sin(t)

    // Adjust the x-coordinate for the shrinking of the east-west distances
    val newX = x / cos(Math.toRadians(latitude))
    return PointDestination(longitude = longitude + newX, latitude = latitude + y)
}
