package com.mapbox.dash.example.ui

import androidx.annotation.DrawableRes
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.driver.R
import com.mapbox.dash.models.WaypointData

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
                Text(
                    text = if (isOffline) "≈ $arrivalTime" else arrivalTime,
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(red = 52, green = 199, blue = 89),
                )

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
                            painter = painterResource(id = stateOfCharge.getStateOfChargeIcon()),
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
                painter = painterResource(
                    if (waypointData.isChargingStation) {
                        R.drawable.ic_navux_trip_progress_charging
                    } else {
                        R.drawable.ic_navux_trip_progress_waypoint
                    },
                ),
                contentDescription = null,
            )
        }

        Image(
            modifier = Modifier
                .size(15.dp)
                .wrapContentSize(unbounded = true)
                .align(BiasAlignment(horizontalBias = 2 * fractionTraveled - 1, verticalBias = 0.0f)),
            painter = painterResource(R.drawable.ic_navux_trip_progress_puck),
            contentDescription = null,
        )
    }
}

@DrawableRes
fun Int.getStateOfChargeIcon(): Int {
    return when {
        this < 0 -> R.drawable.ic_soc_out_of_reach
        this == 0 -> R.drawable.ic_soc_0
        this in 1..10 -> R.drawable.ic_soc_0_10
        this in 11..20 -> R.drawable.ic_soc_10_20
        this in 21..30 -> R.drawable.ic_soc_20_30
        this in 31..40 -> R.drawable.ic_soc_30_40
        this in 41..50 -> R.drawable.ic_soc_40_50
        this in 51..60 -> R.drawable.ic_soc_50_60
        this in 61..70 -> R.drawable.ic_soc_60_70
        this in 71..80 -> R.drawable.ic_soc_70_80
        this in 81..90 -> R.drawable.ic_soc_80_90
        else -> R.drawable.ic_soc_90_100
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
