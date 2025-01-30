package com.mapbox.dash.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mapbox.dash.maneuver.R
import com.mapbox.dash.maneuver.domain.model.DashArrivalManeuver
import com.mapbox.dash.maneuver.domain.model.DashRouteManeuver
import com.mapbox.dash.maneuver.presentation.ui.UpcomingManeuversUiState

@Composable
@SuppressWarnings("MagicNumber")
fun SampleUpcomingManeuversBanner(modifier: Modifier, state: UpcomingManeuversUiState) {
    LazyColumn(
        modifier = modifier
            .background(Color(0xFF018786), shape = RoundedCornerShape(8.dp))
            .padding(20.dp)
            .clickable(onClick = state.onHideUpcomingManeuversClicked),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        state.maneuvers.forEachIndexed { index, maneuver ->
            val keySuffix = state.maneuvers.size - index
            item(key = "maneuver#$keySuffix") {
                when (maneuver) {
                    is DashRouteManeuver -> Maneuver(maneuver)
                    is DashArrivalManeuver -> ArrivalManeuver(maneuver)
                }
            }
        }
    }
}

@Composable
private fun Maneuver(maneuver: DashRouteManeuver) {
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ManeuverTurnIcon(
            modifier = Modifier.size(40.dp),
            iconStyle = R.style.ManeuverTurnIconStylePrimary,
            type = maneuver.maneuver.primary.type,
            degrees = maneuver.maneuver.primary.degrees,
            maneuverModifier = maneuver.maneuver.primary.modifier,
            drivingSide = maneuver.maneuver.primary.drivingSide,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "${maneuver.formattedStepDistance.first} ${maneuver.formattedStepDistance.second}",
                color = Color.Blue,
                maxLines = 1,
            )
            Text(
                text = maneuver.maneuver.primary.text,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun ArrivalManeuver(maneuver: DashArrivalManeuver) {
    Text(
        text = "ARRIVAL to ${maneuver.searchResult?.name}",
        color = Color.White,
    )
}
