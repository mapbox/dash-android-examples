package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.DashCameraTrackingMode.MODE_3D
import com.mapbox.dash.sdk.event.DashCameraTrackingMode.MODE_OVERVIEW
import com.mapbox.dash.sdk.event.DashCameraTrackingMode.MODE_TRACKING
import com.mapbox.dash.sdk.event.DashCameraTrackingMode.MODE_TRACKING_NORTH

@Composable
fun SampleCameraButton(modifier: Modifier) {
    val cameraButtonState = Dash.controller.observeCameraButtonState().collectAsState(null).value ?: return
    SampleCameraButton(
        modifier = modifier,
        isVisible = cameraButtonState.visible,
        cameraTrackingState = cameraButtonState.cameraTrackingState,
        onClick = cameraButtonState.onClick
    )
}

@Composable
private fun SampleCameraButton(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    cameraTrackingState: Int,
    onClick: () -> Unit
) {
    if (isVisible) {
        val icon = when (cameraTrackingState) {
            MODE_TRACKING -> Icons.Default.LocationOn
            MODE_TRACKING_NORTH -> Icons.Default.KeyboardArrowUp
            MODE_3D -> Icons.Default.Person
            MODE_OVERVIEW -> Icons.Default.Star
            else -> Icons.Default.Close
        }
        Image(
            modifier = modifier
                .width(90.dp)
                .height(90.dp)
                .shadow(8.dp, shape = CircleShape)
                .background(androidx.compose.ui.graphics.Color.White)
                .clickable(enabled = true, onClick = onClick)
                .padding(16.dp),
            imageVector = icon,
            colorFilter = null,
            contentDescription = null,
        )
    }
}

@Composable
@Preview
private fun SampleCameraButtonPreview() {
    SampleCameraButton(
        isVisible = true, cameraTrackingState = MODE_TRACKING, onClick = {})
}