@file:SuppressWarnings("MagicNumber")
package com.mapbox.dash.example

import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.mapbox.common.location.Location
import com.mapbox.dash.sdk.coordination.PointDestination
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal fun MaterialButton.bindAction(onClick: suspend () -> Unit) {
    setOnClickListener(null)
    val spec = CircularProgressIndicatorSpec(context, null, 0, R.style.ProgressIndicator)
    val progressIndicatorDrawable = IndeterminateDrawable.createCircularDrawable(context, spec)
    setOnClickListener {
        MainScope().launch {
            val originalIcon = icon
            val originalText = text
            icon = progressIndicatorDrawable
            isEnabled = false
            text = null
            onClick()
            icon = originalIcon
            isEnabled = true
            if (text.isNullOrBlank()) {
                text = originalText
            }
        }
    }
}

internal fun Location.getRandomDestinationAround(): PointDestination {
    val radiusInDegrees: Double = 2000.0 / 111000.0
    val random = Random()
    val u: Double = random.nextDouble()
    val v: Double = random.nextDouble()
    val w = radiusInDegrees * sqrt(u)
    val t = 2 * Math.PI * v
    val x = w * cos(t)
    val y = w * sin(t)

    // Adjust the x-coordinate for the shrinking of the east-west distances
    val newX = x / cos(Math.toRadians(latitude))
    return PointDestination(longitude = longitude + newX, latitude = latitude + y)
}
