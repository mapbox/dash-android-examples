package com.mapbox.dash.example

import android.content.Context
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
import com.mapbox.maps.plugin.LocationPuck
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.LocationPuck3D
import com.mapbox.navigation.ui.maps.R

internal enum class CustomLocationPuck(
    val getLocationPuck: (context: Context) -> LocationPuck?,
) {
    DEFAULT({ null }),
    LEGACY({ context ->
        LocationPuck2D(
            bearingImage = androidx.core.content.ContextCompat.getDrawable(
                context,
                R.drawable.mapbox_navigation_puck_icon,
            ),
        )
    }),
    @Suppress("MaxLineLength", "MagicNumber")
    @OptIn(MapboxExperimental::class)
    DUCK_3D({
        LocationPuck3D(
            modelUri = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF-Embedded/Duck.gltf", // ktlint-disable
            modelScaleExpression = literal(listOf(30, 30, 30)).toJson(),
            modelRotation = listOf(0f, 0f, -90f),
        )
    }),
    ;

    companion object {
        fun names() = values().map { it.name }
    }
}