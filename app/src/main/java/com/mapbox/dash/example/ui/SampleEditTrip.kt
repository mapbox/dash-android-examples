package com.mapbox.dash.example.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.driver.presentation.edittrip.EditTripItem
import com.mapbox.dash.driver.presentation.edittrip.EditTripUiState
import com.mapbox.dash.example.R
import com.mapbox.dash.example.theme.SampleColors
import com.mapbox.dash.example.theme.SampleIcons
import com.mapbox.dash.sdk.map.presentation.ui.BackCloseButtonState

@Composable
@Suppress("LongMethod")
fun SampleEditTrip(state: EditTripUiState) {
    SampleEditTrip(
        items = state.items,
        onOpenFullScreenSearchClick = state.onOpenFullScreenSearchClick,
        onRemoveClick = state.onRemoveClick,
        onDoneClick = state.onDoneClick,
        onItemSwap = state.onItemSwap,
        onAddChargers = state.onAddChargersClick,
        onRemoveChargers = state.onRemoveChargersClick,
        backCloseButtonState = state.backCloseButtonState,
    )
}

@Composable
@Suppress("LongParameterList")
private fun SampleEditTrip(
    items: List<EditTripItem>,
    onOpenFullScreenSearchClick: () -> Unit,
    onRemoveClick: (EditTripItem.Waypoint) -> Unit,
    onDoneClick: () -> Unit,
    onItemSwap: (Int, Int) -> Unit,
    onAddChargers: () -> Unit,
    onRemoveChargers: () -> Unit,
    backCloseButtonState: BackCloseButtonState,
) {
    val widthModifier = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Modifier.width(640.dp)
    } else {
        Modifier.fillMaxWidth()
    }
    Column(modifier = widthModifier.clip(RoundedCornerShape(16.dp))) {
        Header(
            backCloseButtonState,
            onDoneClick,
        )

        Content(
            modifier = Modifier.weight(1f),
            items = items,
            onOpenFullScreenSearchClick = onOpenFullScreenSearchClick,
            onRemoveClick = onRemoveClick,
            onItemSwap = onItemSwap,
            onAddChargers = onAddChargers,
            onRemoveChargers = onRemoveChargers,
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    items: List<EditTripItem>,
    onOpenFullScreenSearchClick: () -> Unit,
    onRemoveClick: (EditTripItem.Waypoint) -> Unit,
    onItemSwap: (Int, Int) -> Unit,
    onAddChargers: () -> Unit,
    onRemoveChargers: () -> Unit,
) {
    val keys = items.map { it.key }
    var waypoints by remember(keys) { mutableStateOf(items) }
    val listState = rememberLazyListState()
    val dragDropState =
        rememberDragDropState(
            key = keys,
            lazyListState = listState,
            onStart = { index ->
                val item = items.getOrNull(index)
                item is EditTripItem.Waypoint || item is EditTripItem.AddNewItem
            },
            onMove = { fromIndex, toIndex ->
                waypoints = waypoints.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
            },
            onEnd = { fromIndex, toIndex -> onItemSwap(fromIndex, toIndex) },
        )
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .dragContainer(dragDropState)
            .background(Color.Black),
        contentPadding = PaddingValues(24.dp),
    ) {
        itemsIndexed(
            waypoints,
            key = { _, item -> item.key },
        ) { index, item ->
            DraggableItem(
                modifier = Modifier.padding(bottom = 2.dp),
                dragDropState = dragDropState,
                index = index,
            ) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp, label = "")

                when (item) {
                    is EditTripItem.AddNewItem -> AddNewWaypoint(
                        elevation,
                        onOpenFullScreenSearchClick,
                    )

                    is EditTripItem.MaxWaypointLimitHint -> Text(
                        modifier = Modifier
                            .background(
                                color = SampleColors.background,
                                shape = RoundedCornerShape(size = 16.dp),
                            ),
                        text = "Exceed ${item.maxCount}",
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                    )

                    is EditTripItem.Waypoint -> WaypointItem(
                        elevation,
                        Modifier.clickable { onRemoveClick(item) },
                        index + 1,
                        item.searchResult.name,
                    )

                    is EditTripItem.TransientWaypoint -> TransientWaypointItem(
                        index + 1,
                        item.searchResult.name,
                    )

                    is EditTripItem.AddChargingStations -> ToggleChargingStationsItem(
                        textResId = R.string.edit_trip_add_charging_stations,
                        onClick = onAddChargers,
                    )

                    is EditTripItem.RemoveChargingStations -> ToggleChargingStationsItem(
                        textResId = R.string.edit_trip_remove_charging_stations,
                        onClick = onRemoveChargers,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddNewWaypoint(elevation: Dp, onOpenFullScreenSearchClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(
                color = SampleColors.background,
                shape = RoundedCornerShape(size = 16.dp),
            )
            .clickable(onClick = onOpenFullScreenSearchClick)
            .padding(16.dp)
            .shadow(elevation)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .size(40.dp),
            colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.7f)),
            imageVector = Icons.Default.Add,
            contentDescription = null,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = "Add new waypoint",
            color = SampleColors.textPrimary.copy(alpha = 0.7f),
            fontSize = 28.sp,
            lineHeight = 34.sp,
            fontWeight = FontWeight.Normal,
        )
        Image(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .padding(8.dp),
            colorFilter = ColorFilter.tint(Color.White.copy(alpha = 0.7f)),
            imageVector = Icons.Default.Menu,
            contentDescription = null,
        )
    }
}

@Composable
private fun WaypointItem(elevation: Dp, removeModifier: Modifier, index: Int, text: String) {
    Row(
        modifier = Modifier
            .background(
                color = SampleColors.background,
                shape = RoundedCornerShape(size = 16.dp),
            )
            .shadow(elevation)
            .padding(16.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = index.toString(),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            )
        }
        Text(
            text = text,
            color = SampleColors.textPrimary,
            modifier = Modifier.weight(1f),
            fontSize = 28.sp,
            lineHeight = 34.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Image(
            modifier = removeModifier
                .size(40.dp)
                .clip(CircleShape)
                .padding(8.dp),
            colorFilter = ColorFilter.tint(SampleColors.textPrimary),
            painter = painterResource(id = SampleIcons.cross),
            contentDescription = null,
        )
        Image(
            modifier = removeModifier
                .size(40.dp)
                .clip(CircleShape)
                .padding(8.dp),
            colorFilter = ColorFilter.tint(Color.White),
            imageVector = Icons.Default.Menu,
            contentDescription = null,
        )
    }
}

@Composable
private fun TransientWaypointItem(index: Int, text: String) {
    Row(
        modifier = Modifier
            .background(
                color = SampleColors.backgroundLight,
                shape = RoundedCornerShape(size = 16.dp),
            )
            .padding(16.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = index.toString(),
                textAlign = TextAlign.Center,
                color = Color.Black,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            )
        }
        Text(
            text = text,
            color = SampleColors.textPrimary,
            modifier = Modifier.weight(1f),
            fontSize = 28.sp,
            lineHeight = 34.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ToggleChargingStationsItem(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    onClick: () -> Unit,
) {
    Text(
        modifier = modifier
            .background(
                color = SampleColors.background,
                shape = RoundedCornerShape(size = 16.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 40.dp)
            .wrapContentHeight(),
        text = stringResource(id = textResId),
        color = SampleColors.textPrimary.copy(alpha = 0.7f),
        fontSize = 28.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
private fun Header(
    backCloseButtonState: BackCloseButtonState,
    onDoneClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(Color(red = 16, green = 18, blue = 23)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
        Text(
            modifier = Modifier.weight(1f),
            text = "Edit trip",
            color = Color.White,
            fontSize = 42.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Normal,
        )
        Text(
            text = "Done",
            color = SampleColors.textPrimary,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            modifier = Modifier
                .padding(end = 20.dp)
                .height(90.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(SampleColors.primary)
                .clickable(onClick = onDoneClick)
                .padding(20.dp)
                .wrapContentSize(),
        )
    }
}

@Composable
@Preview(name = "Light", device = Devices.PIXEL_TABLET)
@SuppressWarnings("MagicNumber")
@SuppressLint("RestrictedApi")
@Preview
internal fun Preview_SampleEditTripExample() {
    MaterialTheme {
        SampleEditTrip(
            items = listOf<EditTripItem>(
                EditTripItem.AddNewItem,
                EditTripItem.RemoveChargingStations,
            ),
            onOpenFullScreenSearchClick = {},
            onRemoveClick = {},
            onDoneClick = {},
            onItemSwap = { _, _ -> },
            onAddChargers = {},
            onRemoveChargers = {},
            backCloseButtonState = BackCloseButtonState(
                isFinalAction = true,
                onBackClicked = {},
                onCloseClicked = {},
            ),
        )
    }
}

@Composable
@Preview(name = "Light", device = Devices.PIXEL_TABLET)
@SuppressWarnings("MagicNumber")
@SuppressLint("RestrictedApi")
@Preview
internal fun Preview_SampleEditTrip_WaypointItem() {
    MaterialTheme {
        WaypointItem(removeModifier = Modifier, index = 4, text = "Whole Foods Market", elevation = 4.dp)
    }
}
