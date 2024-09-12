package com.mapbox.dash.example

import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.config.api.MapStyleLighting.AUTO
import com.mapbox.dash.sdk.config.api.MapStyleLighting.DAWN
import com.mapbox.dash.sdk.config.api.MapStyleLighting.DAY
import com.mapbox.dash.sdk.config.api.MapStyleLighting.DUSK
import com.mapbox.dash.sdk.config.api.MapStyleLighting.NIGHT
import com.mapbox.dash.sdk.config.api.MapStyleMode.MODE_2D
import com.mapbox.dash.sdk.config.api.MapStyleMode.MODE_3D
import com.mapbox.dash.sdk.config.api.MapStyleMode.SATELLITE
import com.mapbox.dash.sdk.config.api.MapStyleTheme.DEFAULT
import com.mapbox.dash.sdk.config.api.MapStyleTheme.FADED
import com.mapbox.dash.sdk.config.api.MapStyleTheme.MONO
import com.mapbox.dash.sdk.config.api.UiModeSettings
import kotlinx.coroutines.flow.MutableStateFlow

class MapStyleViewModel : ViewModel() {

    val mapStyleMode = MutableStateFlow(MODE_3D)
    val mapStyleModeNames = listOf(MODE_3D, MODE_2D, SATELLITE)

    val mapStyleTheme = MutableStateFlow(DEFAULT)
    val mapStyleThemeNames = listOf(DEFAULT, FADED, MONO)

    val mapStyleLighting = MutableStateFlow(AUTO)
    val mapStyleLightingNames = listOf(AUTO, DAWN, DAY, DUSK, NIGHT)

    val uiMode = MutableStateFlow(UiModeSettings.AUTO)
    val uiModeNames = listOf(UiModeSettings.AUTO, UiModeSettings.SYSTEM, UiModeSettings.DARK, UiModeSettings.LIGHT)
}
