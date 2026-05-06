package com.mapbox.dash.showcase.app.ui

import com.mapbox.dash.sdk.config.api.UiMode
import com.mapbox.dash.sdk.config.api.UiModeMapper
import com.mapbox.dash.sdk.config.api.UiModeSettings

internal object DefaultUiModeMapper : UiModeMapper {

    override suspend fun map(
        @UiMode.Type uiMode: String,
        @UiModeSettings.Type uiModeSettings: String,
    ) = uiMode

    override fun toString() = "DefaultUiModeMapper"
}

internal object ReversedUiModeMapper : UiModeMapper {

    override suspend fun map(
        @UiMode.Type uiMode: String,
        @UiModeSettings.Type uiModeSettings: String,
    ) = when (uiMode) {
        UiMode.DUSK -> UiMode.DAWN
        UiMode.DAY -> UiMode.NIGHT
        UiMode.DAWN -> UiMode.DUSK
        UiMode.NIGHT -> UiMode.DAY
        else -> uiMode
    }

    override fun toString() = "ReversedUiModeMapper"
}
