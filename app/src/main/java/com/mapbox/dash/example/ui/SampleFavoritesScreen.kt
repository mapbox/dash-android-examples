package com.mapbox.dash.example.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.example.R
import com.mapbox.dash.fullscreen.search.favorites.FavoritesScreenState
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.search.api.DashFavoriteSearchResult
import com.mapbox.dash.sdk.search.api.DashFavoriteType
import kotlinx.coroutines.launch

@Composable
fun SampleFavoritesScreen(
    modifier: Modifier,
    state: FavoritesScreenState,
) {
    Column(
        modifier = modifier
            .background(Color.Black)
            .systemBarsPadding(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp),
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
                modifier = Modifier.weight(1f),
                text = "Favorites",
                color = Color.White,
                fontSize = 51.sp,
                lineHeight = 72.sp,
                fontWeight = FontWeight.Normal,
            )
            Row(
                modifier = Modifier
                    .height(104.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(red = 28, green = 28, blue = 36))
                    .clickable(onClick = state.onAddFavoriteClicked)
                    .padding(horizontal = 64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 28.dp)
                        .size(72.dp),
                    painter = painterResource(id = R.drawable.ic_add_favorite),
                    contentDescription = null,
                )
                Text(
                    text = "Add Favorite",
                    color = Color.White,
                    fontSize = 36.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
        FavoriteItems(state = state)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.FavoriteItems(state: FavoritesScreenState) {
    val favoriteItems = produceState(initialValue = emptyList<DashFavoriteSearchResult>()) {
        Dash.controller.observeFavorites().collect { value = it }
    }.value
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (favoriteItems.isEmpty()) {
            initialFavoriteItems(state = state)
        } else {
            items(favoriteItems, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .animateItemPlacement()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(red = 28, green = 28, blue = 36))
                        .clickable { state.onFavoriteSelected(item) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val iconId = when (item.favoriteType) {
                        DashFavoriteType.HOME -> R.drawable.ic_favorite_item_home
                        DashFavoriteType.WORK -> R.drawable.ic_favorite_item_work
                        else -> R.drawable.ic_favorite_item
                    }
                    Image(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .size(56.dp),
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                    )
                    FavoritePlaceItemInfo(item)
                    Image(
                        modifier = Modifier
                            .size(128.dp)
                            .clickable { state.onEditFavoriteClicked(item) }
                            .padding(all = 32.dp),
                        painter = painterResource(id = R.drawable.ic_favorite_item_edit),
                        contentDescription = null,
                    )
                    Image(
                        modifier = Modifier
                            .size(128.dp)
                            .clickable {
                                scope.launch { Dash.controller.removeFavoriteItem(item) }
                            }
                            .padding(all = 32.dp),
                        painter = painterResource(id = R.drawable.ic_remove_history_item),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.initialFavoriteItems(state: FavoritesScreenState) {
    item {
        Row(
            modifier = Modifier
                .animateItemPlacement()
                .height(128.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(red = 28, green = 28, blue = 36))
                .clickable(onClick = state.onHomeClicked),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .size(56.dp),
                painter = painterResource(id = R.drawable.ic_favorite_item_home_add),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "Add Home",
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
    item {
        Row(
            modifier = Modifier
                .animateItemPlacement()
                .height(128.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(red = 28, green = 28, blue = 36))
                .clickable(onClick = state.onWorkClicked),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .size(56.dp),
                painter = painterResource(id = R.drawable.ic_favorite_item_work_add),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "Add Work",
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
private fun RowScope.FavoritePlaceItemInfo(item: DashFavoriteSearchResult) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = item.customName ?: item.name,
            color = Color.White,
            fontSize = 36.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Normal,
        )
        item.description?.let { description ->
            Text(
                text = description,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 32.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }
}
