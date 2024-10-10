package com.mapbox.dash.example.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.example.R
import com.mapbox.dash.fullscreen.search.FullScreenSearchState
import com.mapbox.dash.sdk.config.api.SearchCategory

@Composable
fun SampleFullScreenSearch(
    modifier: Modifier,
    state: FullScreenSearchState,
) {
    val showHistory = remember { mutableStateOf(value = false) }
    if (showHistory.value) {
        SampleSearchHistory(
            modifier = modifier,
            onBackClick = { showHistory.value = false },
            onHistoryItemClick = state.onHistoryItemSelected,
        )
        return
    }
    Column(
        modifier = modifier
            .background(color = Color(red = 16, green = 18, blue = 23))
            .systemBarsPadding()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .size(128.dp)
                    .padding(all = 28.dp)
                    .clip(CircleShape)
                    .clickable(onClick = state.onBackClicked),
                painter = painterResource(id = R.drawable.ic_full_screen_search_back),
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .height(104.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = Color(red = 28, green = 28, blue = 36))
                    .clickable(onClick = state.onQueryClicked)
                    .padding(horizontal = 24.dp)
                    .wrapContentHeight(),
                text = "Search by Name",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 42.sp,
                lineHeight = 56.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        GridMainButtons(
            state = state,
            onHistoryClick = { showHistory.value = true },
        )
        GridCategoryButtons(state = state)
    }
}

@Composable
private fun ColumnScope.GridMainButtons(
    state: FullScreenSearchState,
    onHistoryClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GridButton(
            text = "Recents",
            iconId = R.drawable.ic_full_screen_search_recents,
            onClick = onHistoryClick,
        )
        GridButton(
            text = "Add Home",
            iconId = R.drawable.ic_full_screen_search_home,
            onClick = state.onHomeClicked,
        )
        GridButton(
            text = "Add Work",
            iconId = R.drawable.ic_full_screen_search_work,
            onClick = state.onWorkClicked,
        )
        GridButton(
            text = "Favorites",
            iconId = R.drawable.ic_full_screen_search_favorites,
            onClick = state.onFavoritesClicked,
        )
    }
}

@Composable
private fun ColumnScope.GridCategoryButtons(state: FullScreenSearchState) {
    Row(
        modifier = Modifier
            .weight(1f)
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GridButton(
            text = "Charging Station",
            iconId = R.drawable.ic_full_screen_search_charging_station,
            onClick = { state.onCategorySelected(SearchCategory.ChargingStation, "Charging Station") },
        )
        GridButton(
            text = "Hybrid Station",
            iconId = R.drawable.ic_full_screen_search_hybrid_station,
            onClick = { state.onCategorySelected(SearchCategory.GasStation, "Hybrid Station") },
        )
        GridButton(
            text = "Food and Drink",
            iconId = R.drawable.ic_full_screen_search_food_and_drink,
            onClick = { state.onCategorySelected(SearchCategory.Food, "Food and Drink") },
        )
        GridButton(
            text = "Parking",
            iconId = R.drawable.ic_full_screen_search_parking,
            onClick = { state.onCategorySelected(SearchCategory.Parking, "Parking") },
        )
    }
}

@Composable
private fun RowScope.GridButton(
    text: String,
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(red = 28, green = 28, blue = 36))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Image(
            modifier = Modifier.size(96.dp),
            painter = painterResource(id = iconId),
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .height(96.dp)
                .wrapContentHeight(),
            text = text,
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.Normal,
        )
    }
}
