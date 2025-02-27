@file:OptIn(ExperimentalPreviewMapboxNavigationAPI::class)

package com.mapbox.dash.example

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.Dash
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.weather.MapboxWeatherApi
import com.mapbox.navigation.weather.model.WeatherFields
import com.mapbox.navigation.weather.model.WeatherQuery
import com.mapbox.navigation.weather.model.WeatherSystemOfMeasurement
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class, FlowPreview::class)
class WeatherViewModel : ViewModel() {

    private val weatherApi = MapboxWeatherApi()
    private val observeCameraCenter = Dash.controller.observeCameraState()
        .debounce(1.seconds)
        .mapNotNull { it.center }
        .distinctUntilChanged { old, new ->
            val distance = TurfMeasurement.distance(old, new)
            distance < MIN_DISTANCE_IN_KM
        }

    val weatherWarningsAlongRoute = Dash.controller.observeRoutes()
        .map { event ->
            event.routes.firstOrNull()?.let { route ->
                val query = WeatherQuery.WarningsAlongRoute.Builder(route).build()
                weatherApi.getConditions(query).fold(
                    onSuccess = { it },
                    onFailure = {
                        Log.e(TAG, it.message.orEmpty(), it)
                        null
                    },
                )
            } ?: emptyList()
        }

    val weatherConditionAtMapCenter = observeCameraCenter
        .mapNotNull { center ->
            val query = WeatherQuery.Current.Builder(center).build()
            weatherApi.getConditions(query).fold(
                onSuccess = { it.firstOrNull()?.conditions?.firstOrNull() },
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
            val query = WeatherQuery.Daily.Builder(lastWaypoint.location())
                .fields(
                    listOf(
                        WeatherQuery.Daily.Fields.TemperatureMax,
                        WeatherQuery.Daily.Fields.TemperatureMin,
                    ),
                )
                .build()
            weatherApi.getConditions(query).fold(
                onSuccess = {
                    it.firstOrNull()?.conditions?.firstOrNull()?.fields?.let { fields ->
                        val maxTemp = fields.temperatureMax?.toInt()
                        val minTemp = fields.temperatureMin?.toInt()
                        val unit = when (fields.systemOfMeasurement) {
                            WeatherSystemOfMeasurement.Imperial -> "F"
                            WeatherSystemOfMeasurement.Metric -> "C"
                            else -> "C"
                        }

                        DestinationWeatherForecast(
                            text = "H: $maxTemp°$unit    L: $minTemp°$unit",
                        )
                    }
                },
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

internal fun WeatherFields.toIcon(): Int {
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
