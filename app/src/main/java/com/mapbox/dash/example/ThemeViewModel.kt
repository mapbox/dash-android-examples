package com.mapbox.dash.example

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ThemeViewModel : ViewModel() {
    val useCustomTheme = MutableLiveData(false)

    val locationPuck = MutableLiveData(CustomLocationPuck.DEFAULT.name)
}
