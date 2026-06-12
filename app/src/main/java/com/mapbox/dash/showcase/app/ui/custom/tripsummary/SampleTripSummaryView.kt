package com.mapbox.dash.showcase.app.ui.custom.tripsummary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.driver.presentation.end.TripSummaryUiState
import com.mapbox.dash.showcase.app.WeatherController
import kotlin.math.roundToInt

@Composable
fun SampleTripSummaryView(
    modifier: Modifier = Modifier,
    tripSummaryUiState: TripSummaryUiState,
    weatherController: WeatherController,
) {

    val weatherForecast = weatherController.weatherForecastOnDestination.collectAsState(initial = null).value

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
            modifier = Modifier.padding(dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin)),
            items = tripSummaryUiState.tripOverviewItems,
            onWaypointClick = tripSummaryUiState.onWaypointClick,
            onEndOfChargeClick = tripSummaryUiState.onOpenSearchForChargeClick,
        )
        Box {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin))
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin)),
            ) {
                Text(
                    text = stringResource(id = com.mapbox.dash.text.R.string.dash_end_active_guidance_edit_trip),
                    fontSize = 36.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier
                        .height(dimensionResource(id = com.mapbox.dash.theming.R.dimen.button_height))
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(Color(red = 0, green = 122, blue = 255))
                        .clickable(onClick = tripSummaryUiState.onEditTripClick)
                        .padding(dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin))
                        .wrapContentSize(),
                )
                Text(
                    text = stringResource(id = com.mapbox.dash.text.R.string.dash_end_active_guidance),
                    fontSize = 36.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier
                        .height(dimensionResource(id = com.mapbox.dash.theming.R.dimen.button_height))
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(Color(red = 255, green = 59, blue = 48))
                        .clickable(onClick = tripSummaryUiState.onEndActiveGuidanceClick)
                        .padding(dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin))
                        .wrapContentSize(),
                )
            }

            SampleEtaView(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
                arrivalTime = model.legArrivalTime,
                remainingDistance = model.formattedLegDistanceRemaining,
                remainingTime = model.formattedLegTimeRemaining,
                isOffline = model.isOffline,
                stateOfCharge = model.legStateOfCharge?.roundToInt(),
                fractionTraveled = model.fractionTraveled,
                trafficGradientStops = model.trafficGradientStops,
                waypointsData = model.waypointsData,
                weatherForecast = weatherForecast,
            )
        }
    }
}
