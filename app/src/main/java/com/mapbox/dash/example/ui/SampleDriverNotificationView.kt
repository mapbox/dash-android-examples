package com.mapbox.dash.example.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.driver.notification.R
import com.mapbox.dash.driver.notification.presentation.BetterEvRouteType
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.BetterEvRoute
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.BorderCrossing
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.FasterAlternativeAvailable
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.Incident
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.RoadCamera
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.SlowTraffic
import com.mapbox.dash.driver.notification.presentation.DriverNotificationUiState
import com.mapbox.dash.driver.notification.presentation.icon
import com.mapbox.dash.driver.notification.presentation.textResource
import com.mapbox.dash.example.theme.SampleColors
import com.mapbox.dash.example.theme.SampleIcons
import com.mapbox.dash.sdk.base.domain.model.DashIncidentType
import com.mapbox.dash.sdk.config.api.DashIncidentNotificationType
import com.mapbox.dash.sdk.config.api.RoadCameraType.DANGER_ZONE_ENTER
import com.mapbox.dash.sdk.config.api.RoadCameraType.DANGER_ZONE_EXIT
import com.mapbox.dash.sdk.config.api.RoadCameraType.RED_LIGHT
import com.mapbox.dash.sdk.config.api.RoadCameraType.SPEED_CAMERA
import com.mapbox.dash.sdk.config.api.RoadCameraType.SPEED_CAMERA_RED_LIGHT
import com.mapbox.dash.sdk.config.api.RoadCameraType.SPEED_CONTROL_ZONE_ENTER
import com.mapbox.dash.sdk.config.api.RoadCameraType.SPEED_CONTROL_ZONE_EXIT
import com.mapbox.dash.theming.compose.PreviewDashTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@SuppressWarnings("LongMethod", "ComplexMethod")
@Composable
fun SampleDriverNotificationView(
    modifier: Modifier = Modifier,
    uiState: DriverNotificationUiState,
) {
    val context = LocalContext.current
    when (val notification = uiState.driverNotification) {
        is FasterAlternativeAvailable -> {
            DriverNotificationView(
                modifier,
                stringResource(R.string.dash_driver_notification_faster_route_available),
                context.getString(
                    R.string.dash_driver_notification_faster_save_time,
                    notification.diffDuration.toString(DurationUnit.MINUTES),
                ),
                R.drawable.ic_navux_driver_notification_faster_route_available,
                R.string.dash_driver_notification_show,
                { uiState.onAcceptClick(notification) },
                R.string.dash_driver_notification_faster_route_decline,
                { uiState.onDismissClick(notification) },
            )
        }

        is BorderCrossing -> {
            DriverNotificationView(
                modifier,
                context.getString(
                    R.string.dash_driver_notification_border_crossing_title,
                ),
                context.getString(
                    R.string.dash_driver_notification_border_crossing_distance,
                    notification.distanceInMeters.toInt().toString(),
                ),
                R.drawable.ic_navux_driver_notification_border_crossing,
                R.string.dash_driver_notification_show,
                null,
                R.string.dash_driver_notification_dismiss,
                { uiState.onDismissClick(notification) },
            )
        }

        is RoadCamera -> {
            val (iconRes, stringRes) = when (notification.roadCameraType) {
                SPEED_CAMERA -> Pair(
                    R.drawable.ic_navux_driver_notification_speed_camera,
                    R.string.dash_driver_notification_speed_camera,
                )

                SPEED_CAMERA_RED_LIGHT -> Pair(
                    R.drawable.ic_navux_driver_notification_speed_camera_red_light,
                    R.string.dash_driver_notification_speed_camera_red_light,
                )

                RED_LIGHT -> Pair(
                    R.drawable.ic_navux_driver_notification_camera_red_light,
                    R.string.dash_driver_notification_camera_red_light,
                )

                SPEED_CONTROL_ZONE_ENTER -> Pair(
                    R.drawable.ic_navux_driver_notification_speed_control_zone,
                    R.string.dash_driver_notification_speed_control_zone,
                )

                SPEED_CONTROL_ZONE_EXIT -> Pair(
                    R.drawable.ic_navux_driver_notification_speed_control_zone,
                    R.string.dash_driver_notification_speed_control_zone_exit,
                )

                DANGER_ZONE_ENTER -> Pair(
                    R.drawable.ic_navux_driver_notification_danger_zone,
                    R.string.dash_driver_notification_danger_zone,
                )

                DANGER_ZONE_EXIT -> Pair(
                    R.drawable.ic_navux_driver_notification_danger_zone,
                    R.string.dash_driver_notification_danger_zone_exit,
                )

                else -> Pair(null, null)
            }
            if (stringRes != null && iconRes != null) {
                DriverNotificationView(
                    modifier,
                    stringResource(stringRes),
                    notification.distanceInMeters.toInt().toString(),
                    iconRes,
                    0,
                    null,
                    R.string.dash_driver_notification_dismiss,
                    { uiState.onDismissClick(notification) },
                )
            }
        }

        is SlowTraffic -> {
            DriverNotificationView(
                modifier,
                stringResource(R.string.dash_driver_notification_heavy_traffic),
                context.getString(
                    R.string.dash_driver_notification_heavy_traffic_delay,
                    notification.diffDuration.toString(DurationUnit.MINUTES),
                ),
                R.drawable.ic_navux_driver_notification_heavy_traffic,
                0,
                null,
                R.string.dash_driver_notification_dismiss,
                { uiState.onDismissClick(notification) },
            )
        }

        is Incident -> {
            DriverNotificationView(
                modifier,
                notification.textResource(),
                notification.duration?.let { duration ->
                    context.getString(
                        R.string.dash_driver_notification_incident_description,
                        duration.toString(DurationUnit.MINUTES, 0),
                    )
                },
                notification.icon(),
                0,
                null,
                R.string.dash_driver_notification_dismiss,
                { uiState.onDismissClick(notification) },
                notification.timeToDismiss,
            )
        }

        is BetterEvRoute -> {
            val resources = when (notification.betterRouteType) {
                BetterEvRouteType.EXCLUDE_PLANNED_CHARGING -> Triple(
                    SampleIcons.skipCharging,
                    context.getString(R.string.dash_driver_notification_better_ev_route_skip_charging),
                    context.getString(R.string.dash_driver_notification_better_ev_route_skip_charging),
                )

                BetterEvRouteType.INCLUDE_ADDITIONAL_CHARGING -> Triple(
                    SampleIcons.chargingNeeded,
                    context.getString(R.string.dash_driver_notification_better_ev_route_need_charging),
                    context.getString(
                        R.string.dash_driver_notification_better_ev_route_soc_details,
                        notification.minDestinationSoc.toInt(),
                    ),
                )

                else -> Triple(
                    SampleIcons.fastAlternative,
                    context.getString(R.string.dash_driver_notification_better_ev_route_general),
                    context.getString(
                        R.string.dash_driver_notification_better_ev_route_soc_details,
                        notification.minDestinationSoc.toInt(),
                    ),
                )
            }
            DriverNotificationView(
                modifier,
                resources.second,
                resources.third,
                resources.first,
                R.string.dash_driver_notification_show,
                { uiState.onAcceptClick(notification) },
                R.string.dash_driver_notification_dismiss,
                { uiState.onDismissClick(notification) },
            )
        }

        else -> {}
    }
}

@SuppressWarnings("LongParameterList")
@Composable
private fun DriverNotificationView(
    modifier: Modifier,
    title: String,
    description: String?,
    @DrawableRes iconId: Int,
    @StringRes acceptButtonText: Int,
    onAcceptClick: (() -> Unit)? = {},
    @StringRes dismissButtonText: Int,
    onDismissClick: (() -> Unit)? = {},
    timeToDismiss: Duration? = null,
) {

    val defaultPadding = dimensionResource(R.dimen.driver_notification_view_padding)
    Column(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(color = SampleColors.primary.copy(alpha = 0.3f))
            .padding(defaultPadding),
        verticalArrangement = Arrangement.spacedBy(defaultPadding),
    ) {
        DriverNotificationDescription(
            title,
            description,
            iconId,
        )

        DriverNotificationButtonsContainer(
            acceptButtonText,
            onAcceptClick,
            dismissButtonText,
            onDismissClick,
            timeToDismiss,
        )
    }
}

@Composable
private fun DriverNotificationDescription(
    title: String,
    description: String?,
    @DrawableRes iconId: Int,
) {

    Row(
        modifier = Modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(dimensionResource(R.dimen.driver_notification_icon_size)),
            painter = painterResource(iconId),
            contentDescription = null,
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = dimensionResource(R.dimen.driver_notification_view_padding)),
            verticalArrangement = if (description != null) Arrangement.SpaceBetween else Arrangement.Center,
        ) {
            Text(
                text = title,
                color = SampleColors.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
            )
            if (description != null) {
                Text(
                    text = description,
                    color = SampleColors.textPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                )
            }
        }
    }
}

@Composable
private fun DriverNotificationButtonsContainer(
    @StringRes acceptButtonText: Int,
    onAcceptClick: (() -> Unit)? = {},
    @StringRes dismissButtonText: Int,
    onDismissClick: (() -> Unit)? = {},
    timeToDismiss: Duration? = null,
) {

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.driver_notification_view_padding)),
    ) {

        if (onAcceptClick != null) {
            DriverNotificationButton(
                textColor = SampleColors.textPrimary,
                backgroundColor = SampleColors.primary,
                actionButtonText = acceptButtonText,
                onActionButtonClick = onAcceptClick,
            )
        }

        if (onDismissClick != null) {

            val animationStarted = remember { mutableStateOf(false) }
            val progress by animateFloatAsState(
                targetValue = if (animationStarted.value) 1f else 0f,
                animationSpec = tween(
                    durationMillis = (timeToDismiss ?: 15000.milliseconds).toInt(DurationUnit.MILLISECONDS),
                    easing = LinearEasing,
                ),
            ) {
                if (it == 1f) {
                    onDismissClick()
                }
            }

            DriverNotificationButton(
                textColor = SampleColors.primary,
                backgroundColor = SampleColors.primary.copy(alpha = 0.5f),
                actionButtonText = dismissButtonText,
                onActionButtonClick = onDismissClick,
                progress = progress,
            )

            LaunchedEffect(Unit) {
                animationStarted.value = true
            }
        }
    }
}

@SuppressWarnings("LongParameterList")
@Composable
fun DriverNotificationButton(
    modifier: Modifier = Modifier,
    textColor: Color,
    backgroundColor: Color,
    @StringRes actionButtonText: Int,
    onActionButtonClick: () -> Unit = {},
    progress: Float = 0f,
) {
    Text(
        text = stringResource(id = actionButtonText),
        color = textColor,
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .height(dimensionResource(id = R.dimen.button_height))
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = if (progress == 0f) {
                    SolidColor(backgroundColor)
                } else {
                    Brush.horizontalGradient(
                        0f to SampleColors.primary.copy(alpha = 0.5f),
                        progress to SampleColors.primary.copy(alpha = 0.5f),
                        progress to backgroundColor,
                        1f to backgroundColor,
                    )
                },
            )
            .clickable(enabled = true, onClick = onActionButtonClick)
            .wrapContentSize(align = Alignment.Center),
    )
}

@Preview(device = Devices.PIXEL_TABLET, heightDp = 2000, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_TABLET, heightDp = 2000)
@Preview(device = Devices.PIXEL_7, heightDp = 1600)
@Preview(device = Devices.PIXEL_7, heightDp = 1600, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
@SuppressWarnings("MagicNumber")
@SuppressLint("RestrictedApi")
internal fun Preview_All_1() {
    Preview_DriverNotifications(
        listOf(
            DriverNotificationUiState(FasterAlternativeAvailable(1200000.0.milliseconds)),
            DriverNotificationUiState(BorderCrossing("NL", "NLD", 50.0)),
            DriverNotificationUiState(RoadCamera(SPEED_CAMERA, 50.0, 0.0)),
            DriverNotificationUiState(RoadCamera(SPEED_CAMERA_RED_LIGHT, 50.0, 0.0)),
            DriverNotificationUiState(RoadCamera(RED_LIGHT, 50.0, 0.0)),
            DriverNotificationUiState(RoadCamera(SPEED_CONTROL_ZONE_ENTER, 50.0, 0.0)),
            DriverNotificationUiState(RoadCamera(DANGER_ZONE_ENTER, 50.0, 0.0)),
            DriverNotificationUiState(RoadCamera(DANGER_ZONE_EXIT, 50.0, 0.0)),
        ),
    )
}

@Preview(device = Devices.PIXEL_TABLET, heightDp = 2000, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_TABLET, heightDp = 2000)
@Preview(device = Devices.PIXEL_7, heightDp = 1600)
@Preview(device = Devices.PIXEL_7, heightDp = 1600, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
@SuppressWarnings("MagicNumber")
@SuppressLint("RestrictedApi")
internal fun Preview_All_2() {
    Preview_DriverNotifications(
        listOf(
            DriverNotificationUiState(SlowTraffic(12.minutes)),
            DriverNotificationUiState(
                Incident(
                    DashIncidentNotificationType.Accident,
                    DashIncidentType.Accident,
                    1500.milliseconds,
                    1.seconds,
                    100.0,
                ),
            ),
            DriverNotificationUiState(BetterEvRoute(15f, BetterEvRouteType.NO_ADDITIONAL_CHARGING)),
            DriverNotificationUiState(BetterEvRoute(20f, BetterEvRouteType.EXCLUDE_PLANNED_CHARGING)),
            DriverNotificationUiState(BetterEvRoute(25f, BetterEvRouteType.INCLUDE_ADDITIONAL_CHARGING)),
            DriverNotificationUiState(BetterEvRoute(30f, BetterEvRouteType.WITH_THE_SAME_CHARGING)),
        ),
    )
}

@Composable
@SuppressWarnings("MagicNumber")
@SuppressLint("RestrictedApi")
internal fun Preview_DriverNotifications(uiStates: List<DriverNotificationUiState>) {
    PreviewDashTheme {
        LazyColumn(
            modifier = Modifier
                .background(Color(red = 59, green = 66, blue = 82))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = true,
        ) {
            items(uiStates) {
                SampleDriverNotificationView(
                    modifier = Modifier.width(dimensionResource(R.dimen.active_guidance_card_width)),
                    uiState = it,
                )
            }
        }
    }
}
