package com.mapbox.dash.showcase.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.NavigationState
import kotlinx.coroutines.flow.map

class ShowcaseClusterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navigationState = Dash.controller.observeNavigationState()
        val distanceRemaining = Dash.controller.observeRouteProgress().map { it.distanceRemaining }
        setContent {
            ShowcaseClusterView(
                navigationState = navigationState.collectAsState(initial = null).value,
                distanceRemaining = distanceRemaining.collectAsState(initial = null).value,
            )
        }
    }

    @Composable
    private fun ShowcaseClusterView(
        navigationState: NavigationState?,
        distanceRemaining: Float?,
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
        ) {
            ClusterInfo(
                title = "Navigation state:",
                value = when (navigationState) {
                    is NavigationState.FreeDrive -> "FreeDrive"
                    is NavigationState.TripPlanning -> "TripPlanning"
                    is NavigationState.ActiveGuidance -> "ActiveGuidance"
                    is NavigationState.Arrival -> "Arrival"
                    else -> "-"
                },
            )
            ClusterInfo(
                title = "Distance remaining:",
                value = if (distanceRemaining != null) "${distanceRemaining.toInt()} m" else "-",
            )
        }
    }

    @Composable
    private fun ClusterInfo(
        title: String,
        value: String,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = 16.dp),
                text = title,
                fontSize = 18.sp,
                textAlign = TextAlign.End,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(all = 16.dp),
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
