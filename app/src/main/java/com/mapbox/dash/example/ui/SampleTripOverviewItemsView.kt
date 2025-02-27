package com.mapbox.dash.example.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.destination.preview.R
import com.mapbox.dash.destination.preview.domain.model.TripOverviewItem
import com.mapbox.dash.example.DestinationWeatherForecast
import com.mapbox.dash.example.theme.SampleColors
import com.mapbox.dash.models.ChargeData
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.geojson.Point
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Suppress("LongParameterList")
@Composable
internal fun SampleTripOverviewItems(
    modifier: Modifier = Modifier,
    items: List<TripOverviewItem>,
    weatherForecast: DestinationWeatherForecast? = null,
    onYourLocationClick: (() -> Unit)? = null,
    onWaypointClick: ((DashSearchResult) -> Unit)? = null,
    onEndOfChargeClick: ((List<Point>) -> Unit)? = null,
) {
    Column(modifier = modifier) {
        for (item in items) {
            when (item) {
                TripOverviewItem.YourLocation -> TripOverviewItem(
                    iconId = R.drawable.ic_trip_overview_your_location,
                    title = "Your location",
                    onClick = onYourLocationClick,
                )

                is TripOverviewItem.Waypoint -> TripOverviewItem(
                    iconId = R.drawable.ic_trip_overview_waypoint,
                    title = item.searchResult.name,
                    etaMinutes = item.etaMinutes,
                    stateOfCharge = item.stateOfCharge,
                    onClick = onWaypointClick?.forward(item.searchResult),
                    showArrow = true,
                )

                is TripOverviewItem.NoBatteryCharge -> TripOverviewItem(
                    iconId = R.drawable.ic_trip_overview_end_of_charge,
                    title = "Charging stop needed",
                    color = SampleColors.error,
                    onClick = onEndOfChargeClick?.forward(item.routePoints),
                )

                is TripOverviewItem.ChargingStation -> TripOverviewItem(
                    iconId = R.drawable.ic_trip_overview_charging_station,
                    title = item.searchResult.name,
                    etaMinutes = item.etaMinutes,
                    stateOfCharge = item.stateOfCharge,
                    chargeData = item.chargeData,
                    onClick = onWaypointClick?.forward(item.searchResult),
                    showArrow = true,
                )

                is TripOverviewItem.Destination -> TripOverviewItem(
                    iconId = R.drawable.ic_trip_overview_destination,
                    title = item.searchResult.name,
                    etaMinutes = item.etaMinutes,
                    stateOfCharge = item.arrivalStateOfCharge,
                    onClick = onWaypointClick?.forward(item.searchResult),
                    weatherForecast = weatherForecast,
                    showArrow = true,
                )
            }
        }
    }
}

@Composable
private fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = SampleColors.textPrimary,
) {
    androidx.compose.material.Text(
        modifier = modifier,
        text = text,
        color = color,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal,
    )
}

@Suppress("LongParameterList", "LongMethod")
@Composable
private fun TripOverviewItem(
    @DrawableRes iconId: Int,
    title: String,
    color: Color = SampleColors.textPrimary,
    etaMinutes: Double? = null,
    stateOfCharge: Float? = null,
    chargeData: ChargeData? = null,
    onClick: (() -> Unit)? = null,
    weatherForecast: DestinationWeatherForecast? = null,
    showArrow: Boolean = false,
) {
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.background(SampleColors.background),
            painter = painterResource(id = iconId),
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            androidx.compose.material.Text(
                text = title,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 28.sp,
                fontWeight = FontWeight.Normal,
            )
            SampleTripOverviewArrivalInformation(
                etaMinutes = etaMinutes,
                chargeFromPercent = chargeData?.chargeFromPercent ?: stateOfCharge,
                chargeToPercent = chargeData?.chargeToPercent,
                weatherForecast = weatherForecast,
            )
            if (chargeData != null && chargeData.chargeForMin > 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement
                        .spacedBy(4.dp),
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_trip_overview_charge_time),
                        contentDescription = null,
                    )
                    Text(
                        text = chargeData.chargeForMin.minutes.toString(unit = DurationUnit.MINUTES),
                        color = SampleColors.primary.copy(alpha = 0.7f),
                    )
                }
            }
        }
        if (showArrow && onClick != null) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = SampleColors.backgroundLight, shape = CircleShape)
                    .padding(all = 5.dp),
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
            )
        }
    }
}

@Composable
@Suppress("LongMethod")
private fun SampleTripOverviewArrivalInformation(
    etaMinutes: Double?,
    chargeFromPercent: Float?,
    chargeToPercent: Float?,
    weatherForecast: DestinationWeatherForecast? = null,
) {
    if (etaMinutes != null || chargeFromPercent != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (etaMinutes != null) {
                Text(
                    text = etaMinutes.minutes.toString(unit = DurationUnit.MINUTES),
                    color = SampleColors.primary.copy(alpha = 0.7f),
                )
            }
            if (etaMinutes != null && chargeFromPercent != null) {
                Text(
                    text = "·",
                    color = SampleColors.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
            if (chargeFromPercent != null) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(
                        id = chargeFromPercent.toInt().getStateOfChargeIcon()
                    ),
                    contentDescription = null,
                )
                if (chargeFromPercent >= 0) {
                    Text(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .wrapContentHeight(),
                        text = if (chargeToPercent != null) {
                            "$chargeFromPercent% → $chargeToPercent%"
                        } else {
                            "$chargeFromPercent%"
                        },
                        color = SampleColors.primary.copy(alpha = 0.7f),
                    )
                }
            }

            if (weatherForecast != null) {
                Text(
                    text = " · ",
                    color = SampleColors.textPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                Text(
                    text = weatherForecast.text,
                    color = SampleColors.primary.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
    }
}

private fun <T> ((T) -> Unit).forward(arg: T): () -> Unit {
    return { this(arg) }
}
