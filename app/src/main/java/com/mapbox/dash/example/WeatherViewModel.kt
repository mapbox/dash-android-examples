@file:OptIn(ExperimentalPreviewMapboxNavigationAPI::class)

package com.mapbox.dash.example

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.Dash
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.weather.MapboxWeatherApi
import com.mapbox.navigation.weather.model.WeatherCondition
import com.mapbox.navigation.weather.model.WeatherSystemOfMeasurement
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlin.time.Duration.Companion.seconds

@OptIn(FlowPreview::class)
class WeatherViewModel : ViewModel() {

    private val weatherApi = MapboxWeatherApi()
    private val observeCameraCenter = Dash.controller.observeCameraState()
        .debounce(1.seconds)
        .mapNotNull { it.center }
        .distinctUntilChanged { old, new ->
            val distance = TurfMeasurement.distance(old, new)
            distance < MIN_DISTANCE_IN_KM
        }

    val weatherConditionAtMapCenter = observeCameraCenter
        .mapNotNull { center ->
/*            weatherApi.getCondition(center).fold(
                onSuccess = { it },
                onFailure = {
                    Log.e(TAG, it.message.orEmpty(), it)
                    null
                },
            )*/
        }

    val weatherAlertsAtMapCenter = observeCameraCenter
        .mapNotNull { center ->
/*            weatherApi.getWeatherAlerts(center).fold(
                onSuccess = { it },
                onFailure = {
                    Log.e(TAG, it.message.orEmpty(), it)
                    null
                },
            )*/
        }

    val weatherWarningsAlongRoute = Dash.controller.observeRoutes()
        .map { event ->
/*            event.routes.firstOrNull()?.let { route ->
                weatherApi.getWarningsAlongRoute(route).fold(
                    onSuccess = { it },
                    onFailure = {
                        Log.e(TAG, it.message.orEmpty(), it)
                        null
                    },
                )
            } ?: emptyList()*/
        }

    val weatherForecastOnDestination = Dash.controller.observeRoutes()
        .mapNotNull { it.routes.lastOrNull()?.waypoints?.lastOrNull() }
        .distinctUntilChanged()
        .map { lastWaypoint ->
/*            weatherApi.getForecast(lastWaypoint.location()).fold(
                onSuccess = { it },
                onFailure = {
                    Log.e(TAG, it.message.orEmpty(), it)
                    null
                },
            )*/
        }
        .filterNotNull()
        .map { weatherForecast ->
/*            val weatherCondition = weatherForecast.first().condition
            val temperature = weatherCondition.temperature.toInt()
            val weatherIcon = weatherCondition.toIcon()
            val maxTemp = weatherForecast.maxOf { it.condition.temperature }.toInt()
            val minTemp = weatherForecast.minOf { it.condition.temperature }.toInt()
            val unit = when (weatherCondition.systemOfMeasurement) {
                WeatherSystemOfMeasurement.Imperial -> "F"
                WeatherSystemOfMeasurement.Metric -> "C"
                else -> "C"
            }

            DestinationWeatherForecast(
                text = "$temperature °$unit · H: $maxTemp L: $minTemp",
                icon = weatherIcon,
            )*/
        }

    companion object {

        private const val TAG = "WeatherViewModel"
        private const val MIN_DISTANCE_IN_KM = 25
    }
}

internal fun WeatherCondition.toIcon(): Int {
    return 0 /*resolveConditionsToSummary(
        getCloudLevel(this.cloudCover),
        getRainLevel(this.precipitationRate),
    )*/
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
