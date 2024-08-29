package com.mapbox.dash.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.dash.compose.component.Button1
import com.mapbox.dash.destination.preview.domain.model.TripOverviewItem
import com.mapbox.dash.driver.R
import com.mapbox.dash.driver.presentation.end.TripSummaryUiState
import com.mapbox.dash.models.RemainingArrivalMetricType
import com.mapbox.dash.models.TripSummaryModel
import com.mapbox.dash.sdk.search.DashSearchResult
import com.mapbox.dash.theming.compose.AppTheme
import com.mapbox.dash.theming.compose.PreviewDashTheme
import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchResultType

@Composable
fun SampleTripSummaryView(modifier: Modifier = Modifier, tripSummaryUiState: TripSummaryUiState) {

    val model = tripSummaryUiState.tripSummaryModel
    Column(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentHeight(align = Alignment.Bottom)
            .background(
                color = AppTheme.colors.buttonColors.secondary,
                shape = RoundedCornerShape(16.dp),
            ),
    ) {

        SampleTripOverviewItems(
            modifier = Modifier.padding(dimensionResource(R.dimen.default_margin)),
            items = tripSummaryUiState.tripOverviewItems,
            onAddStopClick = tripSummaryUiState.onOpenFullScreenSearchClick,
            onWaypointClick = tripSummaryUiState.onWaypointClick,
            onEndOfChargeClick = tripSummaryUiState.onOpenSearchForChargeClick,
        )
        Box {

            Button1(
                text = stringResource(id = R.string.dash_end_active_guidance_end_route),
                color = AppTheme.colors.textColor.inverted,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(dimensionResource(R.dimen.default_margin))
                    .height(dimensionResource(id = R.dimen.button_height))
                    .clip(shape = AppTheme.shapes.actionButtonBackground)
                    .background(AppTheme.colors.buttonColors.red)
                    .clickable(onClick = tripSummaryUiState.onEndActiveGuidanceClick)
                    .padding(dimensionResource(R.dimen.default_margin))
                    .wrapContentSize(),
            )

            SampleEtaView(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopStart),
                arrivalTime = model.legArrivalTime,
                remainingDistance = model.legDistanceRemaining,
                remainingTime = model.legTimeRemaining,
                isOffline = model.isOffline,
                stateOfCharge = model.legStateOfCharge,
                traveledToRemainingRatio = model.traveledToRemainingRatio,
                trafficGradientStops = model.trafficGradientStops.toTypedArray(),
                waypointsData = model.waypointsData,
            )
        }
    }
}

@Composable
@SuppressWarnings("MagicNumber")
@Preview
internal fun Preview_SampleTripSummaryView() {
    val model = TripSummaryModel(
        traveledToRemainingRatio = 0.2f,
        legDistanceRemaining = "2.1 mi",
        legTimeRemaining = "10 min",
        legArrivalTime = "12:18 pm",
        preferredRemainingArrivalMetricType = RemainingArrivalMetricType.Distance,
        isOffline = false,
        legStateOfCharge = 84,
        trafficGradientStops = listOf(
            (0.150f to Color.Yellow),
            (0.450f to Color.Blue),
            (0.650f to Color.Green),
        ),
    )

    PreviewDashTheme {
        SampleTripSummaryView(
            tripSummaryUiState = TripSummaryUiState(
                tripSummaryModel = model,
                tripOverviewItems = listOf(
                    TripOverviewItem.AddStop,
                    TripOverviewItem.Destination(
                        searchResult = object : DashSearchResult {
                            override val address = null
                            override val coordinate = Point.fromLngLat(1.0, 2.0)
                            override val id = "id-1"
                            override val mapboxId = "mapbox-id-1"
                            override val name = "Mapbox DC Office"
                            override val categories = emptyList<String>()
                            override val description = "description"
                            override val distanceMeters = 0.0
                            override val etaMinutes = 1.0
                            override val pinCoordinate = Point.fromLngLat(1.0, 2.0)
                            override val metadata = null
                            override val type = SearchResultType.POI.toString()
                        },
                        etaMinutes = 10.0,
                        arrivalStateOfCharge = 54,
                    ),
                ),
            ),
        )
    }
}
