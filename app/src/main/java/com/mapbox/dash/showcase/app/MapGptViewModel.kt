package com.mapbox.dash.showcase.app

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import com.mapbox.navigation.mapgpt.LottieMapGptAvatar
import com.mapbox.navigation.mapgpt.useroutput.PrebuiltMapGptAvatars
import kotlinx.coroutines.flow.MutableStateFlow

class MapGptViewModel : ViewModel() {

    // MapGpt
    @SuppressLint("RestrictedApi")
    val availableAvatarNames: List<String> = PrebuiltMapGptAvatars.availableAvatars
        .map { it.name }
        .toMutableList()
        .also {
            it.add(smileBoxPeteAvatar.name)
            it.add(MAP_GPT_UNSET_AVATAR)
        }

    val mapGptAvatarName = MutableStateFlow(MAP_GPT_UNSET_AVATAR)
    val mapGptEnabled = MutableStateFlow(BuildConfig.MAP_GPT_ENABLED)
    val mapGptCustomChatBubble = MutableStateFlow(true)

    @SuppressLint("RestrictedApi")
    val sampleAvatars = PrebuiltMapGptAvatars.avatarMap.toMutableMap().apply {
        put(smileBoxPeteAvatar.name, smileBoxPeteAvatar)
    }

    private companion object {
        private const val MAP_GPT_UNSET_AVATAR = "--"

        /**
         * This is demonstrating the ability to add a custom avatar with your own Lottie animations.
         */
        val smileBoxPeteAvatar = LottieMapGptAvatar(
            name = "SmileBoxPete",
            listeningToUser = com.mapbox.map.gpt.R.raw.ic_mapboxy_listening_to_user,
            userSpeaking = com.mapbox.map.gpt.R.raw.ic_petter_user_speaking,
            aiThinking = com.mapbox.map.gpt.R.raw.ic_smiley_thinking,
            aiSpeaking = com.mapbox.map.gpt.R.raw.ic_mapboxy_speaking,
            aiError = com.mapbox.map.gpt.R.raw.ic_smiley_error,
            aiIdle = com.mapbox.map.gpt.R.raw.ic_petter_listening_to_user,
            aiSleeping = com.mapbox.map.gpt.R.raw.ic_mapboxy_sleeping,
            noMicPermission = com.mapbox.map.gpt.R.raw.ic_smiley_no_mic_permission,
            serviceDisconnected = com.mapbox.map.gpt.R.raw.ic_petter_listening_to_user,
        )
    }
}
