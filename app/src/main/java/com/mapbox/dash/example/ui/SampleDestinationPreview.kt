@file:Suppress("LongMethod")

package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapbox.dash.compose.component.Title5
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.map.presentation.ui.DestinationPreviewUiState
import com.mapbox.dash.sdk.search.api.DashFavoriteType
import com.mapbox.dash.theming.compose.AppTheme
import com.mapbox.dash.view.compose.R
import kotlinx.coroutines.launch

object SampleDestinationPreview {

    @Composable
    operator fun invoke(modifier: Modifier, state: DestinationPreviewUiState) {
        val favorites = remember {
            Dash.controller.observeFavorites()
        }.collectAsState(initial = emptyList())

        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = modifier
                .shadow(elevation = 8.dp, shape = AppTheme.shapes.poiCardBackground)
                .background(AppTheme.colors.backgroundColors.primary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                state.backCloseButtonState?.takeUnless { it.isFinalAction }?.let { backCloseButtonState ->
                    Image(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.card_header_button_size))
                            .clip(CircleShape)
                            .background(AppTheme.colors.buttonColors.primary)
                            .clickable(onClick = backCloseButtonState.onBackClicked)
                            .padding(dimensionResource(id = R.dimen.card_header_button_padding)),
                        painter = painterResource(id = AppTheme.icons.controls.longArrowLeft),
                        contentDescription = null,
                    )
                }
                Title5(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = state.place.content?.origin?.name ?: "Loading...", color = AppTheme.colors.textColor.primary,
                )
                state.backCloseButtonState?.let { backCloseButtonState ->
                    Image(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.card_header_button_size))
                            .clip(CircleShape)
                            .background(AppTheme.colors.buttonColors.primary)
                            .clickable(onClick = backCloseButtonState.onCloseClicked)
                            .padding(dimensionResource(id = R.dimen.card_header_button_padding)),
                        painter = painterResource(id = AppTheme.icons.controls.cross),
                        contentDescription = null,
                    )
                }
            }
            state.place.content?.let { place ->
                place.origin.description?.let {
                    Title5(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = it,
                        color = AppTheme.colors.textColor.primary,
                    )
                } ?: place.origin.address?.let {
                    Title5(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = listOfNotNull(
                            it.country, it.region, it.district, it.place, it.street, it.houseNumber, it.postcode,
                        ).joinToString(", "),
                        color = AppTheme.colors.textColor.primary,
                    )
                }

                place.origin.etaMinutes?.let {
                    Title5(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = "ETA: $it minutes",
                        color = AppTheme.colors.textColor.primary,
                    )
                }

                place.origin.distanceMeters?.let {
                    Title5(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = "Distance: $it meters",
                        color = AppTheme.colors.textColor.primary,
                    )
                }

                val isFavorite = favorites.value.any { it.id == place.origin.id }
                Title5(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            AppTheme.colors.buttonColors.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable {
                            coroutineScope.launch {
                                if (!isFavorite) {
                                    Dash.controller.addFavoriteItem(place.origin, DashFavoriteType.REGULAR)
                                } else {
                                    Dash.controller.removeFavoriteItem(place.origin)
                                }
                            }
                        }
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    text = "${if (isFavorite) "Remove from" else "Add to"} favorites",
                    color = AppTheme.colors.textColor.inverted,
                )
            }

            state.primaryButton?.let {
                Title5(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            AppTheme.colors.buttonColors.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable {
                            it.onClick()
                        }
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    text = it.toString(),
                    color = AppTheme.colors.textColor.inverted,
                )
            }

            state.secondaryButton?.let {
                Title5(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            AppTheme.colors.buttonColors.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable {
                            it.onClick()
                        }
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    text = it.toString(),
                    color = AppTheme.colors.textColor.inverted,
                )
            }
        }
    }
}
