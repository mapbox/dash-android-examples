package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.driver.R
import com.mapbox.dash.example.DestinationWeatherForecast
import com.mapbox.dash.example.toIcon
import com.mapbox.dash.models.WaypointData
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
@Composable
internal fun SampleEtaView(
    modifier: Modifier,
    arrivalTime: String = "12:18 pm",
    remainingDistance: String = "2.1 mi",
    remainingTime: String = "10 min",
    isOffline: Boolean = false,
    stateOfCharge: Int? = null,
    fractionTraveled: Float = 0.6f,
    trafficGradientStops: List<Pair<Float, Color?>> = listOf(
        (0.150f to Color.Yellow),
        (0.450f to Color.Red),
        (0.650f to Color.Blue),
    ),
    waypointsData: List<WaypointData>,
    weatherForecast: DestinationWeatherForecast? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.default_margin),
                end = dimensionResource(id = R.dimen.default_margin),
                top = 16.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start,
            ) {
                Row {
                    Text(
                        text = if (isOffline) "≈ $arrivalTime" else arrivalTime,
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(red = 52, green = 199, blue = 89),
                    )

                    if (weatherForecast != null) {
                        SecondaryText(text = " ∙ ")
                        Image(
                            modifier = Modifier
                                .padding(1.dp)
                                .width(28.dp)
                                .height(28.dp),
                            painter = painterResource(id = weatherForecast.weatherIconCode.toIcon()),
                            contentDescription = null,
                        )
                        SecondaryText(text = "${weatherForecast.temperature.toInt()}° ")
                        SecondaryText(text = weatherForecast.text)
                    }
                }

                Row(
                    modifier = Modifier.height(34.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (!isOffline) {
                        SecondaryText(text = remainingTime)
                        SecondaryText(text = "∙")
                    }

                    SecondaryText(text = remainingDistance)

                    if (stateOfCharge != null) {
                        SecondaryText(text = "∙")

                        Image(
                            modifier = Modifier
                                .padding(1.dp)
                                .width(28.dp)
                                .height(28.dp),
                            painter = painterResource(id = com.mapbox.dash.example.R.drawable.ic_battery),
                            contentDescription = null,
                        )

                        if (stateOfCharge > 0) {
                            SecondaryText(text = "$stateOfCharge%")
                        }
                    }
                }
            }
        }

        SampleTripProgress(
            Modifier.padding(bottom = 8.dp),
            fractionTraveled,
            trafficGradientStops,
            waypointsData,
        )
    }
}

@Composable
private fun SampleTripProgress(
    modifier: Modifier = Modifier,
    fractionTraveled: Float = 0.6f,
    trafficGradientStops: List<Pair<Float, Color?>> = listOf(
        (0.150f to Color.Yellow),
        (0.450f to Color.Red),
        (0.650f to Color.Blue),
    ),
    waypointsData: List<WaypointData>,
) {
    Box(
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.default_margin))
            .height(40.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .height(8.dp)
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        startX = 0f,
                        endX = Float.POSITIVE_INFINITY,
                        colorStops = Array(trafficGradientStops.size) { index ->
                            val (offset, color) = trafficGradientStops[index]
                            offset to (color ?: Color.White.copy(alpha = 0.2f))
                        },
                    ),
                ),
        )

        for (waypointData in waypointsData) {
            Image(
                modifier = Modifier
                    .size(36.dp)
                    .align(BiasAlignment(horizontalBias = waypointData.iconBias, verticalBias = 0.0f)),
                imageVector = Icons.Default.Place,
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = null,
            )
        }

        Image(
            modifier = Modifier
                .size(15.dp)
                .wrapContentSize(unbounded = true)
                .align(BiasAlignment(horizontalBias = 2 * fractionTraveled - 1, verticalBias = 0.0f)),
            imageVector = Icons.AutoMirrored.Default.Send,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null,
        )
    }
}

@Composable
private fun SecondaryText(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Light,
        color = Color(red = 197, green = 197, blue = 201),
    )
}
