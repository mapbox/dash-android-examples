package com.mapbox.dash.example.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.destination.preview.presentation.RoutesOverviewState
import com.mapbox.dash.example.R
import com.mapbox.dash.sdk.map.presentation.ui.BackCloseButtonState
import kotlin.math.roundToLong
import kotlin.time.DurationUnit

@Composable
fun SampleRoutesOverview(
    modifier: Modifier,
    routesOverviewState: RoutesOverviewState,
    backCloseButtonState: BackCloseButtonState?,
) {
    val widthModifier = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        modifier.width(640.dp)
    } else {
        modifier.fillMaxWidth()
    }
    Column(modifier = widthModifier.clip(RoundedCornerShape(16.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .background(Color(red = 16, green = 18, blue = 23)),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (backCloseButtonState != null) {
                Image(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .clickable(onClick = backCloseButtonState.onBackClicked)
                        .padding(28.dp),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
            Text(
                text = "Routes",
                color = Color.White,
                fontSize = 42.sp,
                lineHeight = 56.sp,
                fontWeight = FontWeight.Normal,
            )

            Spacer(modifier = Modifier.weight(1f))
            Image(
                imageVector = Icons.Filled.Create,
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .clickable(onClick = routesOverviewState.onEditClicked)
                    .padding(28.dp),
                contentDescription = null,
                colorFilter = ColorFilter.tint(Color.White),
            )
        }
        val newItemSelected = remember { mutableStateOf(value = false) }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(routesOverviewState.items) { item ->
                SampleRoutesOverviewItem(
                    item = item,
                    onSelect = routesOverviewState.onItemSelected,
                    onNavigateClick = routesOverviewState.onNavigateClicked,
                    newItemSelected = newItemSelected,
                )
            }
        }
    }
}

@Composable
private fun SampleRoutesOverviewItem(
    item: RoutesOverviewState.Item,
    onSelect: (Int) -> Unit,
    onNavigateClick: () -> Unit,
    newItemSelected: MutableState<Boolean>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (item.index == 0) {
                    Color(red = 0, green = 122, blue = 255)
                } else {
                    Color(red = 28, green = 28, blue = 36)
                },
            )
            .clickable(enabled = item.index != 0) {
                newItemSelected.value = true
                onSelect(item.index)
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SampleRoutesOverviewItemPrimaryInfo(item)
        val secondRowText = buildString {
            append("${item.distance.roundToLong()} m")
            if (item.isEcoRoute && item.energyConsumptionSaving != null) {
                append(" – eco – ${item.energyConsumptionSaving} lower emission")
            }
        }
        Text(
            text = secondRowText,
            color = Color.White,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Light,
        )
        if (item.index == 0 && newItemSelected.value) {
            if (item.hasCountryBorderCrossing) {
                Image(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(48.dp),
                    painter = painterResource(id = R.drawable.ic_country_border_crossing),
                    contentDescription = null,
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .clickable(onClick = onNavigateClick)
                    .wrapContentSize(),
                text = "Go Now",
                color = Color.Black,
                fontSize = 36.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun SampleRoutesOverviewItemPrimaryInfo(item: RoutesOverviewState.Item) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = item.duration.toString(DurationUnit.MINUTES),
            color = Color.White,
            fontSize = 36.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
        item.stateOfCharge?.let { stateOfCharge ->
            Text(
                text = "$stateOfCharge %",
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
