package com.mapbox.dash.example

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.weather.api.WeatherApi
import com.mapbox.dash.sdk.weather.api.model.WeatherCondition
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class WeatherViewModel : ViewModel() {

    private val weatherApi: WeatherApi = Dash.weatherApi
    private val observeCameraCenter = Dash.controller.observeCameraState()
        .debounce(1.seconds)
        .mapNotNull { it.center }
        .distinctUntilChanged { old, new ->
            val distance = TurfMeasurement.distance(old, new)
            distance < MIN_DISTANCE_IN_KM
        }

    val weatherConditionAtMapCenter = observeCameraCenter
        .mapNotNull { center ->
            weatherApi.getCondition(center).fold(
                onSuccess = { it },
                onFailure = {
                    Log.e(TAG, it.message.orEmpty(), it)
                    null
                },
            )
        }

    val weatherAlertsAtMapCenter = observeCameraCenter
        .mapNotNull { center ->
            weatherApi.getWeatherAlerts(center).fold(
                onSuccess = { it },
                onFailure = {
                    Log.e(TAG, it.message.orEmpty(), it)
                    null
                },
            )
        }

    val weatherForecastOnDestination = Dash.controller.observeRoutes()
        .mapNotNull { it.routes.lastOrNull()?.waypoints?.lastOrNull() }
        .distinctUntilChanged()
        .map { lastWaypoint ->
            weatherApi.getForecast(lastWaypoint.location()).fold(
                onSuccess = { it },
                onFailure = {
                    Log.e(TAG, it.message.orEmpty(), it)
                    null
                },
            )
        }

    companion object {

        private const val TAG = "WeatherViewModel"
        private const val MIN_DISTANCE_IN_KM = 25
    }
}

internal fun WeatherCondition.toIcon(): Int {
    return resolveConditionsToSummary(
        getCloudLevel(this.cloudCover),
        getRainLevel(this.precipitationRate),
    )
}

private fun resolveConditionsToSummary(
    cloudLevel: ConditionLevel,
    rainLevel: ConditionLevel,
): Int {
    return when {
        rainLevel != ConditionLevel.NONE && cloudLevel == ConditionLevel.HEAVY -> R.drawable.rainy
        rainLevel != ConditionLevel.NONE && cloudLevel != ConditionLevel.HEAVY -> R.drawable.rainy
        rainLevel == ConditionLevel.NONE && cloudLevel == ConditionLevel.HEAVY -> R.drawable.cloudy
        cloudLevel == ConditionLevel.MEDIUM -> R.drawable.partly_cloudy_day
        else -> R.drawable.sunny
    }
}

private fun getCloudLevel(cloudCover: Float?): ConditionLevel {
    return when {
        cloudCover == null -> ConditionLevel.NONE
        cloudCover >= HEAVY_CLOUD_COVER -> ConditionLevel.HEAVY
        cloudCover >= MEDIUM_CLOUD_COVER -> ConditionLevel.MEDIUM
        else -> ConditionLevel.NONE
    }
}

private fun getRainLevel(precipitationRate: Float?): ConditionLevel {
    return when {
        precipitationRate == null -> ConditionLevel.NONE
        precipitationRate >= HEAVY_RAIN_LEVEL -> ConditionLevel.HEAVY
        precipitationRate >= MEDIUM_RAIN_LEVEL -> ConditionLevel.NONE
        else -> ConditionLevel.NONE
    }
}

private enum class ConditionLevel {
    HEAVY,
    MEDIUM,
    NONE,
}

private const val HEAVY_CLOUD_COVER = 70.0
private const val MEDIUM_CLOUD_COVER = 5.0
private const val HEAVY_RAIN_LEVEL = 4.0
private const val MEDIUM_RAIN_LEVEL = 0.1
