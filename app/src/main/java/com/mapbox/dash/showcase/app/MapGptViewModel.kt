package com.mapbox.dash.showcase.app

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MapGptViewModel : ViewModel() {
    val mapGptCustomChatBubble = mutableStateOf(true)
}
