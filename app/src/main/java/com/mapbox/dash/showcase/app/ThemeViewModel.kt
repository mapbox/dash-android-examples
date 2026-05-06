package com.mapbox.dash.showcase.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ThemeViewModel : ViewModel() {
    val dashTheme = MutableStateFlow(CustomDashTheme.CUSTOM.name)
    val locationPuck = MutableStateFlow(CustomLocationPuck.DEFAULT.name)
}
