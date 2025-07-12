@file:SuppressWarnings("LongParameterList", "MagicNumber")

package com.mapbox.dash.example.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.example.R
import com.mapbox.dash.maneuver.presentation.ui.FormattedStepDistance
import com.mapbox.dash.maneuver.presentation.ui.ManeuverUiState
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.NavigationState
import com.mapbox.navigation.tripdata.maneuver.api.MapboxTurnIconsApi
import com.mapbox.navigation.tripdata.maneuver.model.TurnIconResources
import kotlinx.coroutines.flow.map

@Composable
fun SampleGuidanceBanner(modifier: Modifier, state: ManeuverUiState) {
    val defaultMargin = 20.dp
    val isWaypointArrival by remember {
        Dash.controller.observeNavigationState()
            .map {
                it is NavigationState.Arrival
            }
    }.collectAsState(false)
    val maneuver = state.maneuver
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF018786), shape = RoundedCornerShape(8.dp))
            .padding(defaultMargin),
        verticalArrangement = Arrangement.spacedBy(defaultMargin),
    ) {
        if (!isWaypointArrival) {
            ManeuverTurnIcon(
                modifier = Modifier.size(dimensionResource(
                    id = com.mapbox.dash.maneuver.R.dimen.maneuver_view_primary_turn_icon_size)
                ),
                iconStyle = R.style.ManeuverTurnIconStylePrimary,
                type = maneuver.primary.type,
                degrees = maneuver.primary.degrees,
                maneuverModifier = maneuver.primary.modifier,
                drivingSide = maneuver.primary.drivingSide,
            )
        }
        Text(
            text = if (isWaypointArrival) {
                AnnotatedString(stringResource(
                    id = com.mapbox.dash.maneuver.R.string.dash_arrived_text)
                )
            } else {
                buildAnnotatedString {
                    when (val stepDistance = state.stepDistance) {
                        is FormattedStepDistance.Arrival -> append(stepDistance.text)
                        is FormattedStepDistance.DistanceRemaining -> {
                            append(stepDistance.distance)
                            withStyle(SpanStyle(fontSize = 44.sp)) {
                                append(" ")
                                append(stepDistance.unitOfMeasurement)
                            }
                        }
                    }
                }
            },
            color = Color.White,
            fontSize = 68.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun ManeuverTurnIcon(
    modifier: Modifier,
    @StyleRes iconStyle: Int,
    type: String?,
    degrees: Double?,
    maneuverModifier: String?,
    drivingSide: String?,
) {
    val turnIconsApi = remember { MapboxTurnIconsApi(TurnIconResources.defaultIconSet()) }
    val turnIcon = turnIconsApi.generateTurnIcon(
        type, degrees?.toFloat(), maneuverModifier, drivingSide,
    ).value ?: return
    val iconId = turnIcon.icon ?: return
    ManeuverTurnIcon(modifier, iconStyle, iconId, turnIcon.shouldFlipIcon)
}

@Composable
internal fun ManeuverTurnIcon(
    modifier: Modifier,
    @StyleRes iconStyle: Int,
    @DrawableRes iconId: Int,
    flipIcon: Boolean,
) {
    CompositionLocalProvider(LocalContext provides ContextThemeWrapper(LocalContext.current, iconStyle)) {
        Image(
            modifier = modifier.graphicsLayer(rotationY = if (flipIcon) 180f else 0f),
            painter = painterResource(id = iconId),
            contentDescription = null,
        )
    }
}
