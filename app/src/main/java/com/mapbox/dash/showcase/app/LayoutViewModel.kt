package com.mapbox.dash.showcase.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LayoutViewModel : ViewModel() {
    val headlessMode = mutableStateOf(BuildConfig.HEADLESS_MODE_ENABLED)
    val overrideSidebarControls = mutableStateOf(value = false)
}
