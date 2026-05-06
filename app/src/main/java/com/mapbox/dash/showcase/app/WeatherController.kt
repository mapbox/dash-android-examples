package com.mapbox.dash.showcase.app

import android.util.Log
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.weather.MapboxWeatherApi
import com.mapbox.navigation.weather.model.WeatherCondition
import com.mapbox.navigation.weather.model.WeatherIconCode
import com.mapbox.navigation.weather.model.WeatherQuery
import com.mapbox.navigation.weather.model.WeatherSystemOfMeasurement
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@Suppress("ComplexCondition")
@OptIn(ExperimentalPreviewMapboxNavigationAPI::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
class WeatherController(dashNavigationFragmentFlow: Flow<DashNavigationFragment?>) {

    private val weatherApi = MapboxWeatherApi()
    private val observeCameraCenter = dashNavigationFragmentFlow
        .flatMapLatest { it?.observeCameraState() ?: emptyFlow() }
        .debounce(1.seconds)
        .mapNotNull { it.center }
        .distinctUntilChanged { old, new ->
            val distance = TurfMeasurement.distance(old, new)
            distance < MIN_DISTANCE_IN_KM
        }

    val weatherWarningsAlongRoute = Dash.controller.observeRoutes().map { event ->
        event.routes.firstOrNull()?.let { route ->
            val query = WeatherQuery.WarningsAlongRoute.Builder(route).build()
            weatherApi.getConditions(query).getOrElse { exception ->
                Log.e(TAG, exception.message.orEmpty(), exception)
                null
            }
        }.orEmpty()
    }

    val weatherConditionAtMapCenter = observeCameraCenter
        .mapNotNull { center ->
            requestCurrentCondition(center)
        }

    val weatherAlertsAtMapCenter = observeCameraCenter.mapNotNull { center ->
        weatherApi.getWeatherAlerts(center).getOrElse { exception ->
            Log.e(TAG, exception.message.orEmpty(), exception)
            null
        }
    }

    val weatherForecastOnDestination = Dash.controller.observeRoutes()
        .mapNotNull { it.routes.firstOrNull() }
        .distinctUntilChanged()
        .map { primaryRoute ->
            getWeatherConditionAtTimeOfArrival(primaryRoute)
        }

    suspend fun getCurrentWeather(location: Point): DestinationWeatherForecast? {
        val current = Date()
        val dailyResult = requestDailyConditionAtTime(location, current)
        val currentCondition = requestCurrentCondition(location)
        val phrase = currentCondition?.fields?.weatherPhrase
        val temperature = currentCondition?.fields?.temperature
        val iconCode = currentCondition?.fields?.iconCode

        return if (dailyResult != null && phrase != null && temperature != null && iconCode != null) {
            DestinationWeatherForecast(dailyResult, phrase, iconCode, temperature)
        } else null
    }

    private suspend fun requestCurrentCondition(center: Point): WeatherCondition? {
        val query = WeatherQuery.Current.Builder(center)
            .fields(
                listOf(
                    WeatherQuery.Current.Fields.Temperature,
                    WeatherQuery.Current.Fields.IconCode,
                ),
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
        val current = Date()
        val arrivalTime = Date(current.time + routeDuration.seconds.inWholeMilliseconds)

        val dailyResult = requestDailyConditionAtTime(destination, arrivalTime)
        val (phrase, temperature, iconCode) = requestHourlyConditionAtTime(destination, arrivalTime) ?: return null

        return if (dailyResult != null && phrase != null && temperature != null && iconCode != null) {
            DestinationWeatherForecast(dailyResult, phrase, iconCode, temperature)
        } else null
    }

    private suspend fun requestHourlyConditionAtTime(
        destination: Point,
        time: Date,
    ): Triple<String?, Float?, WeatherIconCode?>? {
        val arrivalTimeDelta = Date(time.time + 1.hours.inWholeMilliseconds)

        val hourlyQuery = WeatherQuery.Hourly.Builder(destination)
            .startTime(time)
            .endTime(arrivalTimeDelta)
            .fields(
                listOf(
                    WeatherQuery.Hourly.Fields.Temperature,
                    WeatherQuery.Hourly.Fields.IconCode,
                    WeatherQuery.Hourly.Fields.WeatherPhrase,
                ),
            )
            .build()

        return weatherApi.getConditions(hourlyQuery).fold(
            onSuccess = { results ->
                results.firstOrNull()?.conditions?.firstOrNull()?.fields?.let { fields ->
                    Triple(fields.weatherPhrase, fields.temperature, fields.iconCode)
                }
            },
            onFailure = { throwable ->
                Log.e(TAG, throwable.message.orEmpty(), throwable)
                null
            },
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

                    "H:$maxTemp° L:$minTemp°$unit"
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
