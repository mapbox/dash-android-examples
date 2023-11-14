package com.mapbox.dash.example

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    val dashTheme = MutableLiveData(MainActivity.CustomDashTheme.CUSTOM.name)
    val locationPuck = MutableLiveData(CustomLocationPuck.DEFAULT.name)
}

