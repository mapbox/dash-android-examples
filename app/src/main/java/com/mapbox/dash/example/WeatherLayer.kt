@file:OptIn(MapboxExperimental::class)

package com.mapbox.dash.example

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.imageResource
import com.google.gson.JsonObject
import com.mapbox.dash.example.theme.SampleColors
import com.mapbox.dash.sdk.ev.domain.model.ChargeStatePoint
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMapComposable
import com.mapbox.maps.extension.compose.style.BooleanValue
import com.mapbox.maps.extension.compose.style.ColorValue
import com.mapbox.maps.extension.compose.style.DoubleValue
import com.mapbox.maps.extension.compose.style.PointListValue
import com.mapbox.maps.extension.compose.style.Transition
import com.mapbox.maps.extension.compose.style.layers.FormattedValue
import com.mapbox.maps.extension.compose.style.layers.ImageValue
import com.mapbox.maps.extension.compose.style.layers.generated.IconAnchorValue
import com.mapbox.maps.extension.compose.style.layers.generated.RasterLayer
import com.mapbox.maps.extension.compose.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.compose.style.rememberStyleImage
import com.mapbox.maps.extension.compose.style.sources.GeoJSONData
import com.mapbox.maps.extension.compose.style.sources.generated.rememberGeoJsonSourceState
import com.mapbox.maps.extension.compose.style.sources.generated.rememberImageSourceState
import com.mapbox.maps.extension.style.expressions.dsl.generated.switchCase
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.sources.generated.ImageSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.extension.style.sources.updateImage
import com.mapbox.maps.interactions.FeatureState
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.weather.model.WeatherResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.Date

private const val TAG = "CustomMapLayer"
private const val ID_IMAGE_SOURCE = "image_source-id"
private const val POINTS_URL = "https://opendata.arcgis.com/datasets/01d0ff375695466d93d1fa2a976e2bdd_5.geojson"

@Composable
@MapboxMapComposable
fun WeatherLayer() {
    val bitmaps = listOf(
        ImageBitmap.imageResource(R.drawable.southeast_radar_0).asAndroidBitmap(),
        ImageBitmap.imageResource(R.drawable.southeast_radar_1).asAndroidBitmap(),
        ImageBitmap.imageResource(R.drawable.southeast_radar_2).asAndroidBitmap(),
        ImageBitmap.imageResource(R.drawable.southeast_radar_3).asAndroidBitmap(),
    )
    MapEffect(Unit) {
        val imageSource: ImageSource = it.mapboxMap.getSourceAs(ID_IMAGE_SOURCE)!!
        var index = 0
        while (true) {
            imageSource.updateImage(bitmaps[index])
            delay(1000)
            index = (index + 1) % 4
        }
    }
    RasterLayer(
        sourceState = rememberImageSourceState(sourceId = ID_IMAGE_SOURCE) {
            coordinates = PointListValue(
                Point.fromLngLat(-81.98844, 43.150429),
                Point.fromLngLat(-73.07944, 43.150429),
                Point.fromLngLat(-73.07944, 34.649429),
                Point.fromLngLat(-81.98844, 34.649429)
            )
        }
    )
}

@OptIn(MapboxExperimental::class)
@SuppressLint("IncorrectNumberOfArgumentsInExpression")
@Composable
@MapboxMapComposable
fun FireHydrantsLayer() {
    val day = rememberStyleImage(imageId = "fire-station-day", resourceId = R.drawable.ic_fire_extinguisher_day)
    val night = rememberStyleImage(imageId = "fire-station-night", resourceId = R.drawable.ic_fire_extinguisher_night)

    MapEffect(Unit) {
        it.mapboxMap.style?.let { style ->
            style.addImage(day.imageId, day.image)
            style.addImage(night.imageId, night.image)
        }
    }
    val symbolTextColor = SampleColors.primary

    SymbolLayer(
        sourceState = rememberGeoJsonSourceState {
            data = GeoJSONData(POINTS_URL)
        },
    ) {
        iconImage = ImageValue(
            expression = switchCase {
                boolean {
                    gt {
                        get("MEASURE")
                        literal(2160.0)
                    }
                    literal(false)
                }
                literal(night.imageId)
                literal(day.imageId)
            },
        )
        textField = FormattedValue(
            expression = switchCase {
                boolean {
                    gt {
                        get("MEASURE")
                        literal(2160.0)
                    }
                    literal(false)
                }
                literal("▲")
                literal("▼")
            },
        )
        iconSize = DoubleValue(0.25)
        iconAnchor = IconAnchorValue.BOTTOM
        textColor = ColorValue(symbolTextColor)
        textColorTransition = Transition(durationMillis = 1000)
        textSize = DoubleValue(
            Expression.interpolate {
                linear()
                zoom()
                stop {
                    literal(0.0)
                    literal(10.0)
                }
                stop {
                    literal(10.0)
                    literal(20.0)
                }
            },
        )

        interactionsState.onClicked { interactiveFeature, _ ->
            Log.d(TAG, "Feature clicked: ${interactiveFeature.descriptor}")
            interactiveFeature.setFeatureState(FeatureState { addBooleanState("active", true) })
            true
        }
    }
}

@OptIn(MapboxExperimental::class, ExperimentalPreviewMapboxNavigationAPI::class)
@SuppressLint("IncorrectNumberOfArgumentsInExpression")
@SuppressWarnings("LongMethod")
@Composable
@MapboxMapComposable
fun WeatherAlongRouteBlock(weatherWarningsAlongRoute: Flow<List<WeatherResult>>, clickCallback: (String) -> Unit) {
    val warnings = weatherWarningsAlongRoute.collectAsState(null).value?.map { result ->
        val location = result.location
        result.conditions.map {
            Warning(location, it.warning.orEmpty(), it.dateTime, it.fields.weatherPhrase.orEmpty())
        }
    }?.flatten() ?: return

    if (warnings.isEmpty()) return

    val features = warnings.mapIndexed { index, weatherWarning ->
        val properties = JsonObject()

        val text = "${weatherWarning.text.ifEmpty { weatherWarning.phrase }} at ${weatherWarning.atTime}"
        properties.addProperty("text", text)
        Feature.fromGeometry(weatherWarning.location, properties, index.toString())
    }

    val icon = rememberStyleImage(imageId = "severe-weather-icon", resourceId = R.drawable.cloudy)

    MapEffect(key1 = features.size) {
        it.mapboxMap.getStyle { style ->
            style.addImage(icon.imageId, icon.image)
        }
    }

    SymbolLayer(
        layerId = "weather-along-route",
        sourceState = rememberGeoJsonSourceState(
            key = warnings.size.toString(),
            sourceId = "weather-along-route-source",
        ) {
            data = GeoJSONData(features)
        },
    ) {
        iconAllowOverlap = BooleanValue(true)
        iconImage = ImageValue(icon)
        interactionsState.onClicked { interactiveFeature, _ ->
            val warningText = interactiveFeature.properties.getString("text")
            clickCallback(warningText)
            true
        }
    }
}

@OptIn(MapboxExperimental::class)
@Composable
@MapboxMapComposable
fun EvChargePointBlock(chargePoints: Flow<List<ChargeStatePoint>>, clickCallback: (String) -> Unit) {
    val icon = rememberStyleImage(imageId = "charge-level-icon", resourceId = R.drawable.ic_sample_charge_point)

    MapEffect(Unit) {
        it.mapboxMap.getStyle { style ->
            style.addImage(icon.imageId, icon.image)
        }
    }

    val features = produceState<List<Feature>>(initialValue = emptyList()) {
        chargePoints.collect { points ->
            value = points.map {
                val properties = JsonObject()
                val text = "Charge level = ${it.chargeLevel}"
                properties.addProperty("text", text)
                Feature.fromGeometry(it.point, properties, text)
            }
        }
    }.value

    val geoJsonSource = rememberGeoJsonSourceState {
        generateId = BooleanValue(true)
    }
    geoJsonSource.data = GeoJSONData(features)

    SymbolLayer(
        sourceState = geoJsonSource,
    ) {
        iconAllowOverlap = BooleanValue(true)
        iconImage = ImageValue(icon)
        interactionsState.onClicked { interactiveFeature, _ ->
            val warningText = interactiveFeature.properties.getString("text")
            clickCallback(warningText)
            true
        }
    }
}

private data class Warning(
    val location: Point,
    val text: String,
    val atTime: Date,
    val phrase: String,
)
