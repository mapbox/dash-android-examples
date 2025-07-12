package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.destination.preview.R
import com.mapbox.dash.destination.preview.domain.model.TripOverviewItem
import com.mapbox.dash.example.theme.SampleColors
import com.mapbox.dash.models.ChargeData
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.geojson.Point
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Composable
internal fun SampleTripOverviewItems(
    modifier: Modifier = Modifier,
    items: List<TripOverviewItem>,
    onWaypointClick: (DashSearchResult) -> Unit,
    onEndOfChargeClick: (List<Point>) -> Unit,
) {
    Column(modifier = modifier) {
        for (item in items) {
            when (item) {
                TripOverviewItem.YourLocation -> TripOverviewItem(
                    icon = Icons.AutoMirrored.Default.Send,
                    title = "Your location",
                )

                is TripOverviewItem.Waypoint -> TripOverviewItem(
                    icon = Icons.Default.Place,
                    title = item.searchResult.name,
                    etaMinutes = item.etaMinutes,
                    stateOfCharge = item.stateOfCharge,
                    onClick = { onWaypointClick(item.searchResult) },
                    showArrow = true,
                )

                is TripOverviewItem.NoBatteryCharge -> TripOverviewItem(
                    icon = Icons.Default.Warning,
                    title = "Charging stop needed",
                    color = SampleColors.error,
                    onClick = { onEndOfChargeClick(item.routePoints) },
                )

                is TripOverviewItem.ChargingStation -> TripOverviewItem(
                    icon = Icons.Default.Place,
                    title = item.searchResult.name,
                    etaMinutes = item.etaMinutes,
                    stateOfCharge = item.stateOfCharge,
                    chargeData = item.chargeData,
                    onClick = { onWaypointClick(item.searchResult) },
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
    icon: ImageVector,
    title: String,
    color: Color = SampleColors.textPrimary,
    etaMinutes: Double? = null,
    stateOfCharge: Float? = null,
    chargeData: ChargeData? = null,
    onClick: (() -> Unit)? = null,
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
            imageVector = icon,
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
            )
            if (chargeData != null && chargeData.chargeForMin > 1) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement
                        .spacedBy(4.dp),
                ) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = com.mapbox.dash.example.R.drawable.ic_battery),
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
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                colorFilter = ColorFilter.tint(Color.White),
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
                    painter = painterResource(id = com.mapbox.dash.example.R.drawable.ic_battery),
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
        }
    }
}
