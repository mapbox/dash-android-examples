@file:Suppress("LongMethod", "RestrictedApi")

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
import com.mapbox.dash.example.theme.Body5
import com.mapbox.dash.example.theme.Title5
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.map.presentation.ui.DestinationPreviewUiState
import com.mapbox.dash.sdk.search.api.DashFavoriteType
import com.mapbox.dash.view.compose.R
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

object SampleDestinationPreview {

    @Composable
    operator fun invoke(modifier: Modifier, state: DestinationPreviewUiState) {
        val favorites = remember {
            Dash.controller.observeFavorites()
        }.collectAsState(initial = emptyList())

        val coroutineScope = rememberCoroutineScope()
        Column(
            modifier = modifier
                .shadow(elevation = 8.dp, shape = ExampleAppTheme.shapes.poiCardBackground)
                .background(ExampleAppTheme.colors.backgroundColors.primary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                state.backCloseButtonState?.takeUnless { it.isFinalAction }?.let { backCloseButtonState ->
                    Image(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.card_header_button_size))
                            .clip(CircleShape)
                            .background(ExampleAppTheme.colors.buttonColors.primary)
                            .clickable(onClick = backCloseButtonState.onBackClicked)
                            .padding(dimensionResource(id = R.dimen.card_header_button_padding)),
                        painter = painterResource(id = ExampleAppTheme.icons.controls.longArrowLeft),
                        contentDescription = null,
                    )
                }
                Title5(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = state.place.content?.origin?.name ?: "Loading...", color = ExampleAppTheme.colors.textColor.primary,
                )
                state.backCloseButtonState?.let { backCloseButtonState ->
                    Image(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.card_header_button_size))
                            .clip(CircleShape)
                            .background(ExampleAppTheme.colors.buttonColors.primary)
                            .clickable(onClick = backCloseButtonState.onCloseClicked)
                            .padding(dimensionResource(id = R.dimen.card_header_button_padding)),
                        painter = painterResource(id = ExampleAppTheme.icons.controls.cross),
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
                        color = ExampleAppTheme.colors.textColor.primary,
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
                        color = ExampleAppTheme.colors.textColor.primary,
                    )
                }

                place.arrivalInformation.apply {
                    Title5(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = "${getFormattedEta()} · ${getFormattedDistance()}",
                        color = ExampleAppTheme.colors.textColor.primary,
                    )
                }
                place.chargeData?.takeIf { it.chargeForMin > 1 }?.apply {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement
                            .spacedBy(4.dp),
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(id = ExampleAppTheme.icons.main.charging),
                            contentDescription = null,
                        )
                        Body5(
                            text = chargeForMin.minutes.toString(DurationUnit.MINUTES),
                            color = ExampleAppTheme.colors.textColor.secondary,
                        )
                    }
                }

                val isFavorite = favorites.value.any { it.id == place.origin.id }
                Title5(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            ExampleAppTheme.colors.buttonColors.primary,
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
                    color = ExampleAppTheme.colors.textColor.inverted,
                )
            }

            state.primaryButton?.let {
                Title5(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            ExampleAppTheme.colors.buttonColors.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable {
                            it.onClick()
                        }
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    text = it.toString(),
                    color = ExampleAppTheme.colors.textColor.inverted,
                )
            }

            state.secondaryButton?.let {
                Title5(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            ExampleAppTheme.colors.buttonColors.primary,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .clickable {
                            it.onClick()
                        }
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center,
                    text = it.toString(),
                    color = ExampleAppTheme.colors.textColor.inverted,
                )
            }
        }
    }
}


