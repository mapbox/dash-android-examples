@file:OptIn(ExperimentalPreviewMapboxNavigationAPI::class)

package com.mapbox.dash.example

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mapbox.dash.sdk.Dash
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.weather.MapboxWeatherApi
import com.mapbox.navigation.weather.model.WeatherCondition
import com.mapbox.navigation.weather.model.WeatherIconCode
import com.mapbox.navigation.weather.model.WeatherQuery
import com.mapbox.navigation.weather.model.WeatherSystemOfMeasurement
import com.mapbox.navigation.weather.model.toIconResId
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
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
            requestCurrentCondition(center)
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
        .mapNotNull { it.routes.firstOrNull() }
        .distinctUntilChanged()
        .map { primaryRoute ->
            getWeatherConditionAtTimeOfArrival(primaryRoute)
        }

    suspend fun getCurrentWeather(location: Point): DestinationWeatherForecast? {
        val current = Calendar.getInstance().time
        val dailyResult = requestDailyConditionAtTime(location, current)
        val currentCondition = requestCurrentCondition(location)
        val temperature = currentCondition?.fields?.temperature
        val iconCode = currentCondition?.fields?.iconCode

        return if (dailyResult != null && temperature != null && iconCode != null) {
            DestinationWeatherForecast(text = dailyResult, weatherIconCode = iconCode, temperature = temperature)
        } else null
    }

    private suspend fun requestCurrentCondition(center: Point): WeatherCondition? {
        val query = WeatherQuery.Current.Builder(center)
            .fields(
                listOf(
                    WeatherQuery.Current.Fields.Temperature,
                    WeatherQuery.Current.Fields.IconCode,
                )
            )
            .build()
        return weatherApi.getConditions(query).fold(
            onSuccess = { it.firstOrNull()?.conditions?.firstOrNull() },
            onFailure = {
                Log.e(TAG, it.message.orEmpty(), it)
                null
            },
        )
    }

    private suspend fun getWeatherConditionAtTimeOfArrival(route: NavigationRoute): DestinationWeatherForecast? {
        val destination = route.waypoints?.last()?.location() ?: return null
        val routeDuration = route.directionsRoute.duration()
        val current = Calendar.getInstance().time
        val arrivalTime = Date(current.time + routeDuration.seconds.inWholeMilliseconds)

        val dailyResult = requestDailyConditionAtTime(destination, arrivalTime)
        val (temperature, iconCode) = requestHourlyConditionAtTime(destination, arrivalTime)

        return  if (dailyResult != null && temperature != null && iconCode != null) {
            DestinationWeatherForecast(text = dailyResult, weatherIconCode = iconCode, temperature = temperature)
        } else null
    }

    private suspend fun requestHourlyConditionAtTime(destination: Point, time: Date): Pair<Float?, WeatherIconCode?> {
        val arrivalTimeDelta = Date(time.time + 1.hours.inWholeMilliseconds)

        val hourlyQuery = WeatherQuery.Hourly.Builder(destination)
            .startTime(time)
            .endTime(arrivalTimeDelta)
            .fields(
                listOf(
                    WeatherQuery.Hourly.Fields.Temperature,
                    WeatherQuery.Hourly.Fields.IconCode,
                ),
            )
            .build()

        return weatherApi.getConditions(hourlyQuery).fold(
            onSuccess = {
                it.firstOrNull()?.conditions?.firstOrNull()?.fields?.let { fields ->
                    fields.temperature to fields.iconCode
                } ?: (null to null)
            },
            onFailure = {
                Log.e(TAG, it.message.orEmpty(), it)
                null to null
            }
        )
    }

    private suspend fun requestDailyConditionAtTime(destination: Point, time: Date): String? {
        val arrivalTimeDelta = Date(time.time + 1.days.inWholeMilliseconds)
        val query = WeatherQuery.Daily.Builder(destination)
            .startTime(time)
            .endTime(arrivalTimeDelta)
            .fields(
                listOf(
                    WeatherQuery.Daily.Fields.TemperatureMax,
                    WeatherQuery.Daily.Fields.TemperatureMin,
                ),
            )
            .build()
        return weatherApi.getConditions(query).fold(
            onSuccess = {
                it.firstOrNull()?.conditions?.firstOrNull()?.fields?.let { fields ->
                    val maxTemp = fields.temperatureMax?.toInt()
                    val minTemp = fields.temperatureMin?.toInt()
                    val unit = when (fields.systemOfMeasurement) {
                        WeatherSystemOfMeasurement.Imperial -> "F"
                        WeatherSystemOfMeasurement.Metric -> "C"
                        else -> "C"
                    }

                    return "H:$maxTemp° L:$minTemp°$unit"
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

internal fun WeatherIconCode?.toIcon(): Int {
    return this?.toIconResId() ?: com.mapbox.navigation.weather.R.drawable.mapbox_ic_weather_default
}
