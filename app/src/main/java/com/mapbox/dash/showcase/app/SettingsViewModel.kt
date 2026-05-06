package com.mapbox.dash.showcase.app

import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.config.api.EngineType.BIO_DIESEL
import com.mapbox.dash.sdk.config.api.EngineType.DIESEL
import com.mapbox.dash.sdk.config.api.EngineType.ELECTRIC
import com.mapbox.dash.sdk.config.api.EngineType.GAS
import com.mapbox.dash.sdk.config.api.EngineType.HYBRID
import com.mapbox.dash.sdk.config.api.EngineType.HYDROGEN
import com.mapbox.dash.sdk.config.api.EngineType.PETROL
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsViewModel : ViewModel() {

    val locationSimulationEnabled = MutableStateFlow(false)
    val engineType = MutableStateFlow(GAS)
    val engineTypes =
        listOf(
            GAS,
            PETROL,
            DIESEL,
            BIO_DIESEL,
            ELECTRIC,
            HYDROGEN,
            HYBRID,
        )
}
