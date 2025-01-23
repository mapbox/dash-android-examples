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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mapbox.dash.compose.component.Body5
import com.mapbox.dash.compose.component.Title7
import com.mapbox.dash.destination.preview.R
import com.mapbox.dash.destination.preview.domain.model.TripOverviewItem
import com.mapbox.dash.example.DestinationWeatherForecast
import com.mapbox.dash.models.ChargeData
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
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
                    title = stringResource(id = R.string.dash_trip_overview_your_location),
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
                    title = stringResource(id = R.string.dash_poi_charging_stop_needed),
                    color = ExampleAppTheme.colors.textColor.red,
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

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
@Suppress("LongParameterList", "LongMethod")
@Composable
private fun TripOverviewItem(
    @DrawableRes iconId: Int,
    title: String,
    color: Color = ExampleAppTheme.colors.textColor.primary,
    etaMinutes: Double? = null,
    stateOfCharge: Float? = null,
    chargeData: ChargeData? = null,
    onClick: (() -> Unit)? = null,
    weatherForecast: DestinationWeatherForecast? = null,
    showArrow: Boolean = false,
) {
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(
        modifier = modifier.padding(vertical = dimensionResource(id = R.dimen.poi_card_margin_top_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.default_margin)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.background(ExampleAppTheme.colors.backgroundColors.primary),
            painter = painterResource(id = iconId),
            contentDescription = null,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.poi_card_margin_top_small)),
        ) {
            Title7(
                text = title,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                        .spacedBy(dimensionResource(id = R.dimen.trip_overview_battery_icon_margin)),
                ) {
                    Image(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.trip_overview_battery_icon_size)),
                        painter = painterResource(id = R.drawable.ic_trip_overview_charge_time),
                        contentDescription = null,
                    )
                    Body5(
                        text = chargeData.chargeForMin.minutes.toString(unit = DurationUnit.MINUTES),
                        color = ExampleAppTheme.colors.textColor.secondary,
                    )
                }
            }
        }
        if (showArrow && onClick != null) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = ExampleAppTheme.colors.backgroundColors.secondary, shape = CircleShape)
                    .padding(all = 5.dp),
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null,
            )
        }
    }
}

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
@Composable
private fun SampleTripOverviewArrivalInformation(
    etaMinutes: Double?,
    chargeFromPercent: Float?,
    chargeToPercent: Float?,
    weatherForecast: DestinationWeatherForecast? = null,
) {
    if (etaMinutes != null || chargeFromPercent != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (etaMinutes != null) {
                Body5(
                    text = etaMinutes.minutes.toString(unit = DurationUnit.MINUTES),
                    color = ExampleAppTheme.colors.textColor.secondary,
                )
            }
            if (etaMinutes != null && chargeFromPercent != null) {
                Body5(
                    text = "·",
                    color = ExampleAppTheme.colors.textColor.secondary,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.eta_point_margin)),
                )
            }
            if (chargeFromPercent != null) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.trip_overview_battery_icon_size)),
                    painter = painterResource(id = chargeFromPercent.toInt().getStateOfChargeIcon()),
                    contentDescription = null,
                )
                if (chargeFromPercent >= 0) {
                    Body5(
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.trip_overview_battery_icon_margin))
                            .wrapContentHeight(),
                        text = if (chargeToPercent != null) {
                            "$chargeFromPercent% → $chargeToPercent%"
                        } else {
                            "$chargeFromPercent%"
                        },
                        color = ExampleAppTheme.colors.textColor.secondary,
                    )
                }
            }

            if (weatherForecast != null) {
                Body5(
                    text = " · ",
                    color = ExampleAppTheme.colors.textColor.secondary,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.eta_point_margin)),
                )
                Image(
                    modifier = Modifier
                        .padding(1.dp)
                        .width(28.dp)
                        .height(28.dp),
                    painter = painterResource(id = weatherForecast.icon),
                    contentDescription = null,
                )
                Body5(
                    text = weatherForecast.text,
                    color = ExampleAppTheme.colors.textColor.secondary,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.eta_point_margin)),
                )
            }
        }
    }
}

private fun <T> ((T) -> Unit).forward(arg: T): () -> Unit {
    return { this(arg) }
}
