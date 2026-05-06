@file:OptIn(ExperimentalPreviewMapboxNavigationAPI::class)

package com.mapbox.dash.showcase.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mapbox.navigation.audio.text.TTS_PROVIDER_CORE
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.mapgpt.core.textplayer.TTS_PROVIDER_MAP_GPT
import kotlinx.coroutines.flow.MutableStateFlow

val availableTtsProviders = listOf(TTS_PROVIDER_CORE, TTS_PROVIDER_MAP_GPT)

class VoicePlayerViewModel(application: Application) : AndroidViewModel(application) {
    val remoteTtsProviderKey = MutableStateFlow(availableTtsProviders.first())
}
