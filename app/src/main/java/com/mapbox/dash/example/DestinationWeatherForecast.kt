package com.mapbox.dash.example

import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.weather.model.WeatherIconCode

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
data class DestinationWeatherForecast(
    val text: String,
    val weatherIconCode: WeatherIconCode,
    val temperature: Float,
)
