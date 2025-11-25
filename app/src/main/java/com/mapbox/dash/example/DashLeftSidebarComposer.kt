package com.mapbox.dash.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.mapbox.dash.driver.presentation.DefaultLeftSidebarComposer
import com.mapbox.dash.driver.presentation.SidebarComposer
import com.mapbox.dash.driver.presentation.SidebarScope
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.dash.sdk.event.DashCameraButtonState
import com.mapbox.dash.sdk.event.DashCameraTrackingMode
import kotlinx.coroutines.flow.StateFlow

class DashLeftSidebarComposer(
    private val visible: StateFlow<Boolean>,
    private val override: StateFlow<Boolean>,
    private val dashNavigationFragment: DashNavigationFragment,
) : SidebarComposer by DefaultLeftSidebarComposer {

    @Composable
    override fun SidebarScope.Content() {
        if (!visible.collectAsState().value) return
        if (override.collectAsState().value) {
            CustomCameraButton()
            Feedback()
            VoiceFeedback()
            RangeMap()
            Voice()
            Search()
            Settings()
            Spacer(modifier = Modifier.weight(1f))
            MapGptAvatar()
        } else {
            with(DefaultLeftSidebarComposer) {
                Content()
            }
        }
    }

    @Composable
    private fun CustomCameraButton(modifier: Modifier = Modifier) {
        val cameraButtonState = produceState<DashCameraButtonState?>(initialValue = null) {
            dashNavigationFragment.observeCameraButtonState().collect { value = it }
        }.value ?: return
        if (cameraButtonState.visible) {
            val icon = when (cameraButtonState.cameraTrackingState) {
                DashCameraTrackingMode.MODE_TRACKING -> Icons.Default.LocationOn
                DashCameraTrackingMode.MODE_TRACKING_NORTH -> Icons.Default.KeyboardArrowUp
                DashCameraTrackingMode.MODE_3D -> Icons.Default.Star
                DashCameraTrackingMode.MODE_OVERVIEW -> Icons.Default.Home
                else -> Icons.Default.Close
            }
            Image(
                modifier = modifier
                    .width(90.dp)
                    .height(90.dp)
                    .shadow(8.dp, shape = CircleShape)
                    .background(androidx.compose.ui.graphics.Color.White)
                    .clickable(enabled = true, onClick = cameraButtonState.onClick)
                    .padding(16.dp),
                imageVector = icon,
                contentDescription = null,
            )
        }
    }
}
