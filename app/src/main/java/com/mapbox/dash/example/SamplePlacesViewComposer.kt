@file:SuppressWarnings("MagicNumber", "LongMethod")

package com.mapbox.dash.example

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.dash.compose.component.Body1
import com.mapbox.dash.compose.component.Title4
import com.mapbox.dash.compose.component.Title5
import com.mapbox.dash.example.SamplePlacesViewComposer.PlacesHeader
import com.mapbox.dash.sdk.config.api.UiStates
import com.mapbox.dash.sdk.map.presentation.ui.BackCloseButtonState
import com.mapbox.dash.sdk.map.presentation.ui.PlacesListUiState
import com.mapbox.dash.theming.compose.AppTheme
import com.mapbox.dash.theming.compose.LightDashTheme
import com.mapbox.dash.view.compose.R

object SamplePlacesViewComposer {

    @Composable
    fun isTablet(): Boolean {
        val configuration = LocalConfiguration.current
        return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            configuration.screenWidthDp > 840
        } else {
            configuration.screenWidthDp > 600
        }
    }

    @Composable
    fun ShimmerItem(modifier: Modifier, shape: Shape = RoundedCornerShape(8.dp)) {
        val color = Color(0xFFA1AABB)
        val gradientColors = listOf(color.copy(alpha = 0.1f), color.copy(alpha = 0.3f), color.copy(alpha = 0.1f))
        Spacer(modifier = modifier.background(Brush.horizontalGradient(gradientColors), shape))
    }

    @Composable
    fun InfoCard(number: Int, title: String, eta: String, onItemSelected: () -> Unit) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    AppTheme.colors.backgroundColors.tertiary,
                    shape = AppTheme.shapes.searchPanelBackground,
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable(onClick = onItemSelected),
            ) {
                Body1(
                    text = "#$number",
                    color = AppTheme.colors.textColor.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Title4(
                    text = title,
                    color = AppTheme.colors.textColor.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Title5(
                    text = "ETA: $eta",
                    color = AppTheme.colors.textColor.secondary,
                )
            }
        }
    }

    @Composable
    internal fun SearchResultPlaceholder() {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(
                    AppTheme.colors.backgroundColors.tertiary,
                    shape = AppTheme.shapes.searchPanelBackground,
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                ShimmerItem(
                    modifier = Modifier
                        .height(24.dp)
                        .width(370.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerItem(
                    modifier = Modifier
                        .height(34.dp)
                        .width(370.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                ShimmerItem(
                    modifier = Modifier
                        .height(34.dp)
                        .width(370.dp),
                )
            }
        }
    }

    @Composable
    internal fun PlacesHeader(
        title: String,
        backCloseButtonState: BackCloseButtonState?,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
        ) {
            backCloseButtonState?.let { state ->
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.card_header_button_size))
                        .clip(CircleShape)
                        .background(AppTheme.colors.buttonColors.primary)
                        .clickable(onClick = backCloseButtonState.onBackClicked)
                        .wrapContentSize()
                        .size(dimensionResource(id = R.dimen.card_header_icon_size)),
                    painter = painterResource(id = AppTheme.icons.controls.longArrowLeft),
                    contentDescription = null,
                )
            }
            Title5(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = title, color = AppTheme.colors.textColor.primary,
            )
            backCloseButtonState?.let { state ->
                Image(
                    modifier = Modifier
                        .weight(0.5f)
                        .size(dimensionResource(id = R.dimen.card_header_button_size))
                        .clip(CircleShape)
                        .background(AppTheme.colors.buttonColors.primary)
                        .clickable(onClick = state.onCloseClicked)
                        .wrapContentSize()
                        .size(dimensionResource(id = R.dimen.card_header_icon_size)),
                    painter = painterResource(id = AppTheme.icons.controls.cross),
                    contentDescription = null,
                )
            }
        }
    }

    @Composable
    operator fun invoke(
        lazyListState: LazyListState,
        placesListUiState: PlacesListUiState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(fraction = if (isTablet()) 0.5f else 1f)
                .background(AppTheme.colors.backgroundColors.primary, shape = AppTheme.shapes.poiCardBackground)
        ) {
            PlacesHeader(
                title = placesListUiState.title.content.orEmpty(),
                backCloseButtonState = placesListUiState.backCloseButtonState,
            )
            placesListUiState.items.UiStates(
                loading = {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = lazyListState,
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
                content = { content, loading, error ->
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = lazyListState,
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

@Preview(device = Devices.AUTOMOTIVE_1024p)
@Composable
private fun HeaderPreview() {
    LightDashTheme {
        PlacesHeader(title = "Coffee", backCloseButtonState = null)
    }
}



