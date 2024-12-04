package com.mapbox.dash.example

import androidx.lifecycle.ViewModel
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

    val uiMode = MutableStateFlow(UiModeSettings.AUTO)
    val uiModeNames = listOf(
        UiModeSettings.AUTO,
        UiModeSettings.SYSTEM,
        UiModeSettings.DAWN,
        UiModeSettings.DAY,
        UiModeSettings.DUSK,
        UiModeSettings.NIGHT,
    )
}
