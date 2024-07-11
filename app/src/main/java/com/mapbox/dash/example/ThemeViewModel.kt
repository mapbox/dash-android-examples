package com.mapbox.dash.example

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class ThemeViewModel : ViewModel() {
    val dashTheme = MutableStateFlow(MainActivity.CustomDashTheme.CUSTOM.name)
    val locationPuck = MutableStateFlow(CustomLocationPuck.DEFAULT.name)
}

