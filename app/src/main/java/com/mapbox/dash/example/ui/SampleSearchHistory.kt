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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
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
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.search.api.DashSearchResult
import kotlinx.coroutines.launch

@Composable
fun SampleSearchHistory(
    onBackClick: () -> Unit,
    onHistoryItemClick: (DashSearchResult) -> Unit,
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
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
                    .clickable(onClick = onBackClick),
                painter = painterResource(id = R.drawable.ic_full_screen_search_back),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "Recents",
                color = Color.White,
                fontSize = 51.sp,
                lineHeight = 72.sp,
                fontWeight = FontWeight.Normal,
            )
            Text(
                modifier = Modifier
                    .width(456.dp)
                    .height(104.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(red = 28, green = 28, blue = 36))
                    .clickable {
                        scope.launch { Dash.controller.cleanHistory() }
                    }
                    .wrapContentSize(),
                text = "Clear History",
                color = Color.White,
                fontSize = 36.sp,
                lineHeight = 48.sp,
                fontWeight = FontWeight.Normal,
            )
        }
        HistoryItems(
            historyItems = produceState(initialValue = emptyList<DashSearchResult>()) {
                Dash.controller.observeHistory().collect { value = it }
            }.value,
            onHistoryItemClick = onHistoryItemClick,
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ColumnScope.HistoryItems(
    historyItems: List<DashSearchResult>,
    onHistoryItemClick: (DashSearchResult) -> Unit,
) {
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(start = 24.dp, top = 16.dp, end = 24.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(historyItems, key = { it.id }) { item ->
            Row(
                modifier = Modifier
                    .animateItemPlacement()
                    .height(128.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(red = 28, green = 28, blue = 36))
                    .clickable { onHistoryItemClick(item) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .size(56.dp),
                    painter = painterResource(id = R.drawable.ic_history_item),
                    contentDescription = null,
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = item.name,
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
                Image(
                    modifier = Modifier
                        .size(128.dp)
                        .clickable {
                            scope.launch { Dash.controller.removeHistoryItem(item) }
                        }
                        .padding(all = 32.dp),
                    painter = painterResource(id = R.drawable.ic_remove_history_item),
                    contentDescription = null,
                )
            }
        }
    }
}
