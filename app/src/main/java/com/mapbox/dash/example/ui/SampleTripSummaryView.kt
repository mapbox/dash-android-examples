package com.mapbox.dash.example.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.driver.R
import com.mapbox.dash.driver.presentation.end.TripSummaryUiState
import com.mapbox.dash.example.WeatherViewModel

@Composable
fun SampleTripSummaryView(
    modifier: Modifier = Modifier,
    tripSummaryUiState: TripSummaryUiState,
    weatherViewModel: WeatherViewModel,
) {

    val weatherForecast = weatherViewModel.weatherForecastOnDestination.collectAsState(null).value

    val defaultMargin = dimensionResource(R.dimen.default_margin)
    val model = tripSummaryUiState.tripSummaryModel
    Column(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentHeight(align = Alignment.Bottom)
            .background(
                color = Color(red = 16, green = 18, blue = 23),
                shape = RoundedCornerShape(16.dp),
            ),
    ) {

        SampleTripOverviewItems(
            modifier = Modifier.padding(start = defaultMargin, end = defaultMargin, top = defaultMargin),
            items = tripSummaryUiState.tripOverviewItems,
            onWaypointClick = tripSummaryUiState.onWaypointClick,
            onEndOfChargeClick = tripSummaryUiState.onOpenSearchForChargeClick,
            weatherForecast = null /*weatherForecast*/,
        )

        SampleEtaView(
            modifier = Modifier.fillMaxWidth(),
            arrivalTime = model.legArrivalTime,
            remainingDistance = model.legDistanceRemaining,
            remainingTime = model.legTimeRemaining,
            isOffline = model.isOffline,
            stateOfCharge = model.legStateOfCharge,
            fractionTraveled = model.fractionTraveled,
            trafficGradientStops = model.trafficGradientStops,
            waypointsData = model.waypointsData,
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = defaultMargin,
                        end = defaultMargin,
                        bottom = defaultMargin
                    ),
            horizontalArrangement = Arrangement.spacedBy(defaultMargin)
        ) {

            SampleTripSummaryActionButton(
                textId = R.string.dash_end_active_guidance_edit_trip,
                backgroundColor = Color(red = 0, green = 122, blue = 255),
                onClick = tripSummaryUiState.onEditTripClick
            )

            SampleTripSummaryActionButton(
                textId = R.string.dash_end_active_guidance_end_route,
                backgroundColor = Color(red = 255, green = 59, blue = 48),
                onClick = tripSummaryUiState.onEndActiveGuidanceClick
            )

        }
    }
}

@Composable
private fun RowScope.SampleTripSummaryActionButton(
    @StringRes textId: Int,
    backgroundColor: Color,
    onClick: () -> Unit,
) {
    Text(
        text = stringResource(id = textId),
        fontSize = 36.sp,
        lineHeight = 48.sp,
        fontWeight = FontWeight.Normal,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .weight(1f)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(dimensionResource(R.dimen.default_margin)),
    )
}

