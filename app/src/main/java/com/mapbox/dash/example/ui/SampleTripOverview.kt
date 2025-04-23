package com.mapbox.dash.example.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.destination.preview.domain.model.TripOverviewItem
import com.mapbox.dash.destination.preview.presentation.tripoverview.TripOverviewUiState
import com.mapbox.dash.example.theme.SampleColors
import com.mapbox.dash.sdk.map.presentation.ui.BackCloseButtonState
import com.mapbox.dash.sdk.search.api.DashSearchResult

@Composable
fun SampleTripOverview(
    state: TripOverviewUiState,
) {
    SampleTripOverview(
        state.buttonState,
        state.items,
        state.etaDistance.etaMinutes,
        state.onNavigateClick,
        state.onWaypointClick,
        state.onEditTripClick,
        state.onAddChargerClick,
    )
}

@Composable
private fun SampleTripOverview(
    buttonState: BackCloseButtonState?,
    items: List<TripOverviewItem>,
    etaMinutes: Double?,
    onNavigateClick: () -> Unit,
    onWaypointClick: (DashSearchResult) -> Unit,
    onEditTripClick: () -> Unit,
    onAddChargerClick: () -> Unit,
) {
    val widthModifier = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Modifier.width(640.dp)
    } else {
        Modifier.fillMaxWidth()
    }

    Column(
        modifier = widthModifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(red = 16, green = 18, blue = 23)),
    ) {
        Header(buttonState, onEditTripClick)
        Content(items, etaMinutes, onWaypointClick, onNavigateClick, onAddChargerClick)
    }
}

@Composable
private fun Content(
    items: List<TripOverviewItem>,
    etaMinutes: Double?,
    onWaypointClick: (DashSearchResult) -> Unit,
    onNavigateClick: () -> Unit,
    onAddChargerClick: () -> Unit,
) {
    SampleTripOverviewItems(
        items = items,
        onWaypointClick = onWaypointClick,
        onEndOfChargeClick = { onAddChargerClick() },
    )
    etaMinutes?.let {
        ArrivalInformationView(it)
    }
    NavigateButton(onNavigateClick)
}

@Composable
private fun NavigateButton(onNavigateClick: () -> Unit) {
    Text(
        text = "Navigate",
        color = SampleColors.textPrimary,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        modifier = Modifier
            .padding(20.dp)
            .height(90.dp)
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(16.dp))
            .background(SampleColors.primary)
            .clickable(onClick = onNavigateClick)
            .wrapContentSize(),
    )
}

@Composable
private fun ArrivalInformationView(etaMinutes: Double) {
    Text(
        modifier = Modifier
            .padding(28.dp),
        text = "${etaMinutes.toInt()} minutes remain",
        textAlign = TextAlign.Center,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
        color = Color(red = 52, green = 199, blue = 89),
    )
}

@Composable
private fun Header(
    backCloseButtonState: BackCloseButtonState?,
    onEditTrip: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (backCloseButtonState != null) {
            Image(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .clickable(onClick = backCloseButtonState.onCloseClicked)
                    .padding(28.dp),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
            )
        }
        Text(
            modifier = Modifier.weight(1f),
            text = "Trip overview",
            color = Color.White,
            fontSize = 42.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Normal,
        )
        Text(
            text = "Edit",
            color = SampleColors.textPrimary,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            modifier = Modifier
                .padding(end = 20.dp)
                .height(90.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(SampleColors.primary)
                .clickable(onClick = onEditTrip)
                .padding(20.dp)
                .wrapContentSize(),
        )
    }
}

@SuppressWarnings("MagicNumber")
@SuppressLint("RestrictToUsage")
@Preview(name = "Light", device = Devices.PIXEL_TABLET)
@Composable
internal fun Preview_SampleEditTrip() {
    MaterialTheme {
        SampleTripOverview(
            items = stubTripOverviewItems,
            buttonState = BackCloseButtonState(
                isFinalAction = true,
                onCloseClicked = {},
                onBackClicked = {},
            ),
            etaMinutes = 4.0,
            onWaypointClick = {},
            onNavigateClick = {},
            onEditTripClick = {},
            onAddChargerClick = {},
        )
    }
}

private val stubTripOverviewItems = listOf(
    TripOverviewItem.YourLocation,
)
