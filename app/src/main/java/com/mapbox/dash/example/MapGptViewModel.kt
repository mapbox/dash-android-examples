package com.mapbox.dash.example

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.mapbox.dash.sdk.Dash
import com.mapbox.navigation.mapgpt.api.LottieMapGptAvatar
import com.mapbox.navigation.mapgpt.useroutput.PrebuiltMapGptAvatars
import kotlinx.coroutines.flow.map

class MapGptViewModel : ViewModel() {

    // MapGpt

    val availableAvatarNames: List<String> = PrebuiltMapGptAvatars.availableAvatars
        .map { it.name }
        .toMutableList()
        .also {
            it.add(smileBoxPeteAvatar.name)
            it.add(MAP_GPT_UNSET_AVATAR)
        }

    val mapGptAvatarName: LiveData<String> = Dash.observeConfig()
        .map { it.mapGptConfig.avatar?.name ?: MAP_GPT_UNSET_AVATAR }
        .asLiveData()

    val sampleAvatars = PrebuiltMapGptAvatars.avatarMap.toMutableMap().apply {
        put(smileBoxPeteAvatar.name, smileBoxPeteAvatar)
    }

    val isMapGptEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isMapGptKeyboardModeEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    private companion object {
        private const val MAP_GPT_UNSET_AVATAR = "--"

        /**
         * This is demonstrating the ability to add a custom avatar with your own Lottie animations.
         */
        val smileBoxPeteAvatar = LottieMapGptAvatar(
            name = "SmileBoxPete",
            listeningToUser = com.mapbox.dash.sdk.R.raw.ic_mapboxy_listening_to_user,
            userSpeaking = com.mapbox.dash.sdk.R.raw.ic_petter_user_speaking,
            aiThinking = com.mapbox.dash.sdk.R.raw.ic_smiley_thinking,
            aiSpeaking = com.mapbox.dash.sdk.R.raw.ic_mapboxy_speaking,
            aiError = com.mapbox.dash.sdk.R.raw.ic_smiley_error,
            aiIdle = com.mapbox.dash.sdk.R.raw.ic_petter_listening_to_user,
            aiSleeping = com.mapbox.dash.sdk.R.raw.ic_mapboxy_sleeping,
            noMicPermission = com.mapbox.dash.sdk.R.raw.ic_smiley_no_mic_permission,
            serviceDisconnected = com.mapbox.dash.sdk.R.raw.ic_petter_listening_to_user,
        )
    }
}
