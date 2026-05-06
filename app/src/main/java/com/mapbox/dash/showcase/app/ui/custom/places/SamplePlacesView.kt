@file:Suppress("MagicNumber")

package com.mapbox.dash.showcase.app.ui.custom.places

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.sdk.config.api.UiStates
import com.mapbox.dash.sdk.map.presentation.ui.PlacesListUiState
import com.mapbox.dash.showcase.app.theme.SampleColors

object SamplePlacesView {

    @Composable
    fun ShimmerItem(modifier: Modifier, shape: Shape = RoundedCornerShape(8.dp)) {
        val color = Color(0xFFA1AABB)
        val gradientColors = listOf(color.copy(alpha = 0.1f), color.copy(alpha = 0.3f), color.copy(alpha = 0.1f))
        Spacer(modifier = modifier.background(Brush.horizontalGradient(gradientColors), shape))
    }

    @Composable
    fun InfoCard(number: Int, title: String, eta: String, onItemSelected: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .background(SampleColors.backgroundLight)
                .padding(16.dp)
                .clickable(onClick = onItemSelected),
        ) {
            Text(
                // body 1
                textAlign = TextAlign.Center,
                text = "#$number",
                color = SampleColors.textPrimary.copy(alpha = 0.4f),
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text( // body 4
                textAlign = TextAlign.Center,
                text = title,
                color = SampleColors.textPrimary.copy(alpha = 0.9f),
                fontSize = 36.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                // title 5
                textAlign = TextAlign.Center,
                text = "ETA: $eta",
                color = SampleColors.textPrimary.copy(alpha = 0.5f),
                fontSize = 26.sp,
                fontWeight = FontWeight.Light,
            )
        }
    }

    @Composable
    internal fun SearchResultPlaceholder(modifier: Modifier = Modifier) {
        Column(modifier = modifier) {
            ShimmerItem(
                modifier = Modifier
                    .height(24.dp)
                    .width(370.dp),
            )
            val secondRowModifier = Modifier
                .padding(top = 4.dp)
                .height(34.dp)
            ShimmerItem(modifier = secondRowModifier.defaultMinSize(440.dp))
        }
    }

    @Composable
    operator fun invoke(
        placesListUiState: PlacesListUiState,
        modifier: Modifier,
    ) {
        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SampleColors.background)
                .padding(16.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                placesListUiState.backCloseButtonState.takeUnless { it.isFinalAction }?.let { backCloseButtonState ->
                    Image(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SampleColors.primary)
                            .clickable(onClick = backCloseButtonState.onBackClicked)
                            .padding(12.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        colorFilter = ColorFilter.tint(Color.White),
                        contentDescription = null,
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = placesListUiState.title.content ?: "",
                    color = SampleColors.textPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(SampleColors.primary)
                        .clickable(onClick = placesListUiState.backCloseButtonState.onCloseClicked)
                        .padding(12.dp),
                    imageVector = Icons.Default.Close,
                    colorFilter = ColorFilter.tint(Color.White),
                    contentDescription = null,
                )
            }
            placesListUiState.items.UiStates(
                loading = {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SampleColors.background)
                            .clip(RoundedCornerShape(16.dp))
                            .padding(top = 16.dp, bottom = 8.dp),
                        state = placesListUiState.lazyListState,
                        contentPadding = PaddingValues(bottom = 8.dp),
                    ) {
                        repeat(times = 3) {
                            item {
                                SearchResultPlaceholder()
                            }
                        }
                    }
                },
                failure = {},
                content = { content, _, _ ->
                    LaunchedEffect(content.size) {
                        placesListUiState.itemsVisibilityChanged(content.map { it.id })
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SampleColors.background)
                            .clip(RoundedCornerShape(16.dp))
                            .padding(top = 16.dp, bottom = 8.dp),
                        state = placesListUiState.lazyListState,
                        contentPadding = PaddingValues(bottom = 8.dp),
                    ) {
                        content.forEachIndexed { index, result ->
                            item {
                                InfoCard(
                                    number = index + 1,
                                    title = result.name,
                                    eta = result.distanceMeters?.toLong()
                                        .toString() + "m · " + result.etaMinutes?.toLong() + "min",
                                    onItemSelected = { placesListUiState.itemSelected(index) },
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}
