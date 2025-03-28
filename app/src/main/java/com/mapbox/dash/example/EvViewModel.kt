package com.mapbox.dash.example

import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.Dash

class EvViewModel : ViewModel() {

    @SuppressWarnings("MagicNumber")
    val chargePoints = Dash.controller.observeAvailableChargeStatePoints(
        listOf(25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10),
    )
}
