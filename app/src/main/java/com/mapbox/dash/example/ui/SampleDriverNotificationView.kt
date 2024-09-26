package com.mapbox.dash.example.ui

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.dash.compose.component.Body4
import com.mapbox.dash.compose.component.Button1
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.FasterAlternativeAvailable
import com.mapbox.dash.driver.notification.presentation.DriverNotificationUiState
import com.mapbox.dash.driver.notification.R
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.BorderCrossing
import com.mapbox.dash.driver.notification.presentation.DashDriverNotification.SlowTraffic
import com.mapbox.dash.theming.compose.AppTheme
import com.mapbox.dash.theming.compose.PreviewDashTheme
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

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
                AppTheme.icons.driverNotification.fastAlternative,
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
                    notification.country,
                ),
                context.getString(
                    R.string.dash_driver_notification_border_crossing_distance,
                    notification.distanceInMeters.toInt().toString(),
                ),
                AppTheme.icons.driverNotification.borderCrossing,
                R.string.dash_driver_notification_show,
                null,
                R.string.dash_driver_notification_dismiss,
                { uiState.onDismissClick(notification) },
            )
        }

        is SlowTraffic -> {
            DriverNotificationView(
                modifier,
                stringResource(R.string.dash_driver_notification_heavy_traffic),
                context.getString(
                    R.string.dash_driver_notification_heavy_traffic_delay,
                    notification.diffDuration.toString(DurationUnit.MINUTES),
                ),
                AppTheme.icons.driverNotification.heavyTraffic,
                0,
                null,
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
    description: String,
    @DrawableRes iconId: Int,
    @StringRes acceptButtonText: Int,
    onAcceptClick: (() -> Unit)? = {},
    @StringRes dismissButtonText: Int,
    onDismissClick: (() -> Unit)? = {},
) {

    val defaultPadding = dimensionResource(R.dimen.driver_notification_view_padding)
    Column(
        modifier = modifier
            .background(
                color = AppTheme.colors.buttonColors.secondary,
                shape = AppTheme.shapes.driverNotificationBackground,
            )
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
        )
    }
}

@Composable
private fun DriverNotificationDescription(
    title: String,
    description: String,
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
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Button1(text = title)
            Body4(text = description)
        }
    }
}

@Composable
private fun DriverNotificationButtonsContainer(
    @StringRes acceptButtonText: Int,
    onAcceptClick: (() -> Unit)? = {},
    @StringRes dismissButtonText: Int,
    onDismissClick: (() -> Unit)? = {},
) {

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.driver_notification_view_padding)),
    ) {

        if (onAcceptClick != null) {
            DriverNotificationButton(
                textColor = AppTheme.colors.textColor.inverted,
                backgroundColor = AppTheme.colors.buttonColors.primary,
                actionButtonText = acceptButtonText,
                onActionButtonClick = onAcceptClick,
            )
        }

        if (onDismissClick != null) {

            val animationStarted = remember { mutableStateOf(false) }
            val progress by animateFloatAsState(
                targetValue = if (animationStarted.value) 1f else 0f,
                animationSpec = tween(durationMillis = 15000, easing = LinearEasing),
            ) {
                if (it == 1f) { onDismissClick() }
            }

            DriverNotificationButton(
                textColor = AppTheme.colors.textColor.accent,
                backgroundColor = AppTheme.colors.buttonColors.secondaryNew,
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
    Button1(
        text = stringResource(id = actionButtonText),
        color = textColor,
        modifier = modifier
            .height(dimensionResource(id = R.dimen.button_height))
            .fillMaxWidth()
            .background(
                shape = AppTheme.shapes.driverNotificationButtonBackground,
                brush = if (progress == 0f) {
                    SolidColor(backgroundColor)
                } else {
                    Brush.horizontalGradient(
                        0f to AppTheme.colors.buttonColors.secondaryProgress,
                        progress to AppTheme.colors.buttonColors.secondaryProgress,
                        progress to backgroundColor,
                        1f to backgroundColor,
                    )
                },
            )
            .clickable(enabled = true, onClick = onActionButtonClick)
            .wrapContentSize(align = Alignment.Center),
    )
}

@Preview(device = Devices.PIXEL_TABLET, heightDp = 1080, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(device = Devices.PIXEL_TABLET, heightDp = 1080)
@Preview(device = Devices.PIXEL_7)
@Preview(device = Devices.PIXEL_7, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
@SuppressWarnings("MagicNumber")
internal fun Preview_DriverNotifications() {
    val uiStates = listOf(
        DriverNotificationUiState(FasterAlternativeAvailable(1200000.0.milliseconds)),
        DriverNotificationUiState(BorderCrossing("Germany", 100.0)),
        DriverNotificationUiState(SlowTraffic(1.minutes)),
    )
    PreviewDashTheme {
        LazyColumn(
            modifier = Modifier
                .background(AppTheme.colors.backgroundColors.tertiary)
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
