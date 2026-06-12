package com.mapbox.dash.showcase.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LayoutViewModel : ViewModel() {
    val overrideSidebarControls = mutableStateOf(value = false)
}
