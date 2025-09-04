@file:OptIn(ExperimentalPreviewMapboxNavigationAPI::class)

package com.mapbox.dash.example.ui

import android.content.res.Configuration
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.compose.noIndicationClickable
import com.mapbox.dash.compose.shadow
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.DashCameraTrackingMode
import com.mapbox.dash.sdk.event.NavigationState
import com.mapbox.dash.sdk.map.presentation.markers.RouteCalloutUiState
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.ViewAnnotationAnchorConfig
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.OnViewAnnotationUpdatedListener
import com.mapbox.maps.viewannotation.annotatedLayerFeature
import com.mapbox.maps.viewannotation.annotationAnchors
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun SampleRouteCalloutView(state: RouteCalloutUiState) {
    val cameraMode = Dash.controller.observeCameraState().collectAsState(null).value?.mode ?: return
    val navigationState = Dash.controller.observeNavigationState().collectAsState(null).value ?: return
    val isActiveGuidance =
        navigationState is NavigationState.ActiveGuidance || navigationState is NavigationState.Arrival
    val isOverview = cameraMode == DashCameraTrackingMode.MODE_OVERVIEW

    if (isActiveGuidance && !isOverview && state.callout.isPrimary) return

    val duration = if (!isOverview && isActiveGuidance) {
        state.callout.durationDifferenceWithPrimary
    } else {
        state.callout.route.directionsRoute.duration().seconds
    }

    val type = when {
        isActiveGuidance && !isOverview -> {
            if (state.callout.durationDifferenceWithPrimary.isNegative()) {
                "Slower"
            } else {
                "Faster"
            }
        }
        state.isBest -> "Best"
        state.isFastest -> "Fastest"
        state.isSuggested -> "Suggested"
        else -> null
    }.orEmpty()

    state.isEcoRoute

    val prefix = if (state.hasTollRoad) "$ " else ""
    val label = "$prefix $type".trim()

    SampleRouteCalloutViewAnnotation(
        duration,
        label,
        state.layerId,
        state.callout.isPrimary,
        state.isEcoRoute,
        state.onClick,
    )
}

@Composable
private fun SampleRouteCalloutViewAnnotation(
    duration: Duration,
    label: String,
    layerId: String,
    isPrimary: Boolean,
    isEcoRoute: Boolean,
    onClick: () -> Unit,
) {
    // key prevents annotation contents from mixing
    key(
        duration,
        label,
        layerId,
        isEcoRoute,
        isPrimary,
    ) {
        val anchorState = remember { mutableStateOf(value = ViewAnnotationAnchor.TOP_LEFT) }
        ViewAnnotation(
            options = viewAnnotationOptions {
                ignoreCameraPadding(true)
                annotatedLayerFeature(layerId)
                annotationAnchors(
                    { anchor(ViewAnnotationAnchor.TOP_LEFT) },
                    { anchor(ViewAnnotationAnchor.TOP_RIGHT) },
                    { anchor(ViewAnnotationAnchor.BOTTOM_RIGHT) },
                    { anchor(ViewAnnotationAnchor.BOTTOM_LEFT) },
                )
            },
            onUpdatedListener = object : OnViewAnnotationUpdatedListener {
                override fun onViewAnnotationAnchorUpdated(view: View, anchor: ViewAnnotationAnchorConfig) {
                    anchorState.value = anchor.anchor
                }
            },
        ) {
            SampleRouteCalloutViewContent(
                anchor = anchorState.value,
                duration = duration,
                primary = isPrimary,
                eco = isEcoRoute,
                label = label,
                onClick = onClick,
            )
        }
    }
}

private data class CalloutShape(private val anchor: ViewAnnotationAnchor) : Shape {

    private val matrix = Matrix()

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path()
        val rect = (size / density.density).toRect()
        path.addRect(rect.deflate(delta = 7f))
        matrix.reset()
        path.addArrow(size)
        matrix.scale(density.density, density.density)
        path.transform(matrix)
        return Outline.Generic(path)
    }

    private fun Path.addArrow(size: Size) {
        when (anchor) {
            ViewAnnotationAnchor.TOP_LEFT -> {
                // nothing to do
            }

            ViewAnnotationAnchor.TOP_RIGHT -> {
                matrix.translate(x = size.width)
                matrix.scale(x = -1f)
            }

            ViewAnnotationAnchor.BOTTOM_RIGHT -> {
                matrix.translate(x = size.width, y = size.height)
                matrix.scale(x = -1f, y = -1f)
            }

            ViewAnnotationAnchor.BOTTOM_LEFT -> {
                matrix.translate(y = size.height)
                matrix.scale(y = -1f)
            }

            else -> return
        }

        moveTo(x = 20f, y = 7f)
        lineTo(x = 1.1f, y = 0.1f)
        lineTo(x = 7f, y = 20f)
    }
}

@Composable
private fun SampleRouteCalloutViewContent(
    anchor: ViewAnnotationAnchor,
    duration: Duration,
    label: String = "",
    eco: Boolean = false,
    primary: Boolean = false,
    onClick: () -> Unit = {},
) {
    val shape = CalloutShape(anchor)
    val shapeModifier = Modifier
        .shadow(shape = shape, clip = false)
        .background(
            color = if (primary) Color.Blue else Color.White,
            shape = shape,
        )
        .noIndicationClickable(onClick = onClick)
    Column(
        modifier = shapeModifier.padding(horizontal = 17.dp, vertical = 13.dp),
    ) {
        Text(
            duration.inWholeMinutes.minutes.absoluteValue.toString(),
            color = if (primary) Color.White else Color.Black
        )

        if (label.isNotEmpty() || eco) {
            val text = buildString {
                append(label)
                if (label.isNotEmpty() && eco) append(" – ")
                if (eco) append("eco")
            }
            Text(
                text,
                color = if (primary) Color.White else Color.Black,
            )
        }
    }
}

@Preview(name = "Light_Tablet", device = Devices.TABLET)
@Preview(name = "Dark_Phone", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun Preview_DefaultRouteCalloutView() {
    MaterialTheme {
        Column(modifier = Modifier.background(Color.Black)) {
            SampleRouteCalloutViewContent(
                anchor = ViewAnnotationAnchor.TOP_LEFT,
                duration = 5.minutes + 30.seconds,
                eco = true,
                label = "Fastest",
            )
            SampleRouteCalloutViewContent(
                anchor = ViewAnnotationAnchor.TOP_RIGHT,
                duration = 3.hours + 30.minutes,
                primary = true,
            )
            SampleRouteCalloutViewContent(
                anchor = ViewAnnotationAnchor.BOTTOM_RIGHT,
                eco = true,
                duration = -1.hours,
            )
            SampleRouteCalloutViewContent(
                anchor = ViewAnnotationAnchor.BOTTOM_LEFT,
                duration = 3.hours + 25.minutes + 30.seconds,
                label = "Fastest",
            )
        }
    }
}
