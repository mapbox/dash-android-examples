@file:Suppress("LongMethod", "RestrictedApi")

package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.example.DestinationWeatherForecast
import com.mapbox.dash.example.WeatherViewModel
import com.mapbox.dash.example.theme.SampleColors
import com.mapbox.dash.example.toIcon
import com.mapbox.dash.models.ArrivalInformationFormatter
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.config.api.UiStates
import com.mapbox.dash.sdk.map.presentation.ui.DestinationPreviewUiState
import com.mapbox.dash.sdk.search.api.DashFavoriteType
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
object SampleDestinationPreview {

    @Suppress("CyclomaticComplexMethod")
    @Composable
    operator fun invoke(modifier: Modifier, state: DestinationPreviewUiState, weatherViewModel: WeatherViewModel) {

        val weatherAtDestination = remember { mutableStateOf<DestinationWeatherForecast?>(null) }
        LaunchedEffect(state.place.content?.origin?.id) {
            weatherAtDestination.value = state.place.content?.origin?.coordinate?.let {
                weatherViewModel.getCurrentWeather(it)
            }
        }

        val favorites = remember {
            Dash.controller.observeFavorites()
        }.collectAsState(initial = emptyList())

        val coroutineScope = rememberCoroutineScope()

        Column(
            modifier = modifier
                .clip(RoundedCornerShape(16.dp))
                .background(SampleColors.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                state.backCloseButtonState?.takeUnless { it.isFinalAction }?.let { backCloseButtonState ->
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
                    text = state.place.content?.origin?.name ?: "Loading...",
                    color = SampleColors.textPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                state.backCloseButtonState?.let { backCloseButtonState ->
                    Image(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SampleColors.primary)
                            .clickable(onClick = backCloseButtonState.onCloseClicked)
                            .padding(12.dp),
                        imageVector = Icons.Default.Close,
                        colorFilter = ColorFilter.tint(Color.White),
                        contentDescription = null,
                    )
                }
            }
            state.place.content?.let { place ->
                place.origin.description?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = it,
                        color = SampleColors.textPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                    )
                } ?: place.origin.address?.let {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = listOfNotNull(
                            it.country, it.region, it.district, it.place, it.street, it.houseNumber, it.postcode,
                        ).joinToString(", "),
                        color = SampleColors.textPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }

                place.arrivalInformation.apply {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Start,
                        text = "${getFormattedEta()} · ${getFormattedDistance()}",
                        color = SampleColors.textPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }

                weatherAtDestination.value?.let { weather ->
                    Row {
                        Image(
                            modifier = Modifier
                                .padding(1.dp)
                                .width(32.dp)
                                .height(32.dp),
                            painter = painterResource(id = weather.weatherIconCode.toIcon()),
                            contentDescription = null,
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            textAlign = TextAlign.Start,
                            text = "${weather.temperature.toInt()}° · ${weather.text}",
                            color = SampleColors.textPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }

                place.chargeData?.takeIf { it.chargeForMin > 1 }?.apply {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement
                            .spacedBy(4.dp),
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.Warning,
                            colorFilter = ColorFilter.tint(Color.White),
                            contentDescription = null,
                        )
                        Text(
                            text = StubDistanceAndTimeFormatter.formatDuration(
                                duration = chargeForMin.minutes,
                                truncateDurationUnit = DurationUnit.MINUTES,
                            ),
                            color = SampleColors.textPrimary.copy(alpha = 0.7f),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Normal,
                        )
                    }
                }

                place.weather.UiStates(
                    loading = {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp))
                    },
                    failure = {
                        // Do nothing
                    },
                    content = { content, _, _ ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Image(
                                painter = painterResource(content.iconResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(end = 4.dp),
                            )

                            @Composable
                            fun Text(text: String) {
                                Text(
                                    text = text,
                                    color = SampleColors.textPrimary.copy(alpha = 0.7f),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Light,
                                )
                            }

                            Text(text = content.weatherNow)
                            Text(text = ".")
                            Text(text = content.weatherHigh)
                            Text(text = content.weatherLow)
                        }
                    },
                )

                val isFavorite = favorites.value.any { it.id == place.origin.id }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            SampleColors.primary,
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
                    color = SampleColors.textPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                )
            }

            state.primaryButton?.let {
                PrimaryButton(it)
            }

            state.secondaryButton?.let {
                SecondaryButton(it)
            }
        }
    }

    private object StubDistanceAndTimeFormatter : ArrivalInformationFormatter {

        override fun formatDistance(distanceInMeters: Double) = "$distanceInMeters m"

        override fun formatDuration(duration: Duration, truncateDurationUnit: DurationUnit): String =
            duration.toString(truncateDurationUnit)

        override fun formatTime(time: Date): String = time.toString()
    }
}

@Composable
private fun ColumnScope.PrimaryButton(primary: DestinationPreviewUiState.ActionButton.Primary) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                SampleColors.primary,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable {
                primary.onClick()
            }
            .padding(16.dp)
            .align(Alignment.CenterHorizontally),
        textAlign = TextAlign.Center,
        text = primary.toString(),
        color = SampleColors.textPrimary,
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal,
    )
}

@Composable
private fun ColumnScope.SecondaryButton(secondary: DestinationPreviewUiState.ActionButton.Secondary) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                SampleColors.primary,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable {
                secondary.onClick()
            }
            .padding(16.dp)
            .align(Alignment.CenterHorizontally),
        textAlign = TextAlign.Center,
        text = secondary.toString(),
        color = SampleColors.textPrimary.copy(alpha = 0.9f),
        fontSize = 32.sp,
        fontWeight = FontWeight.Normal,
    )
}
