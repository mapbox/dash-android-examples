package com.mapbox.dash.showcase.app

import android.content.Context
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.plugin.LocationPuck
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.LocationPuck3D
import com.mapbox.navigation.ui.maps.R

internal enum class CustomLocationPuck(
    val getLocationPuck: (context: Context) -> LocationPuck?,
) {
    DEFAULT({ null }),
    LEGACY({ _ ->
        LocationPuck2D(
            bearingImage = ImageHolder.from(
                R.drawable.mapbox_navigation_puck_icon,
            ),
        )
    }),
    @Suppress("MaxLineLength", "MagicNumber")
    @OptIn(MapboxExperimental::class)
    DUCK_3D({
        LocationPuck3D(
            modelUri = "https://raw.githubusercontent.com/KhronosGroup/glTF-Sample-Models/master/2.0/Duck/glTF-Embedded/Duck.gltf", // ktlint-disable
            modelScale = listOf(30f, 30f, 30f),
            modelRotation = listOf(0f, 0f, -90f),
        )
    }),
    ;

    companion object {
        fun names() = entries.map { it.name }
    }
}
