package com.mapbox.dash.example

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mapbox.dash.sdk.Dash
import com.mapbox.navigation.mapgpt.useroutput.PrebuiltMapGptAvatars
import kotlinx.coroutines.flow.map

class MapGptAvatarViewModel : ViewModel() {

    val availableAvatarNames: List<String> = PrebuiltMapGptAvatars.availableAvatars
        .map { it.name }
        .toMutableList()
        .also {
            it.add(MAP_GPT_DEFAULT_AVATAR)
        }

    val mapGptAvatarName: LiveData<String> = Dash.observeConfig()
        .map { it.mapGptConfig.avatar?.name ?: MAP_GPT_DEFAULT_AVATAR }
        .asLiveData()

    val sampleAvatars = PrebuiltMapGptAvatars.avatarMap

    private companion object {
        private const val MAP_GPT_DEFAULT_AVATAR = "Default"
    }
}