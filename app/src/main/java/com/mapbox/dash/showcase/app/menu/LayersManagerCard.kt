package com.mapbox.dash.showcase.app.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.dash.sdk.base.layer.DashMapStyleLayer
import com.mapbox.dash.sdk.base.layer.DashMapStyleLayersConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull

private const val TRANSIT_LAYER_ID = "transit-label"
private const val POI_LAYER_ID = "poi-label"

@Composable
fun LayersManagerCard(dashNavigationFragmentFlow: Flow<DashNavigationFragment?>) {
    val togglesVisible = rememberSaveable { mutableStateOf(false) }
    val distractingElementsEnabled = rememberSaveable { mutableStateOf(false) }
    val transitLayerVisible = rememberSaveable { mutableStateOf(true) }
    val poiLayerVisible = rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(distractingElementsEnabled.value, transitLayerVisible.value, poiLayerVisible.value) {
        dashNavigationFragmentFlow.filterNotNull().collect { dashNavigationFragment ->
            val layers = if (distractingElementsEnabled.value) {
                emptyList()
            } else {
                listOf(
                    getTransitLayer(visible = transitLayerVisible.value),
                    getPoiLayer(visible = poiLayerVisible.value),
                )
            }
            val mapStyleLayersConfig = DashMapStyleLayersConfig(layers)
            dashNavigationFragment.setMapStyleLayersConfig(mapStyleLayersConfig)
        }
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(alpha = 0x44, red = 0x00, green = 0x99, blue = 0xcc)),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(alpha = 0x88, red = 0x00, green = 0x99, blue = 0xcc))
                .clickable { togglesVisible.value = !togglesVisible.value }
                .padding(8.dp)
                .wrapContentSize(),
            text = "MAP STYLE LAYERS MANAGER",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        AnimatedVisibility(togglesVisible.value) {
            Column {
                MenuSwitch(
                    text = "DISTRACTING MAP ELEMENTS HIDDEN IN ACTIVE GUIDANCE",
                    state = distractingElementsEnabled,
                )
                MenuSwitch(
                    text = "TRANSIT",
                    state = transitLayerVisible,
                    enabled = !distractingElementsEnabled.value,
                )
                MenuSwitch(
                    text = "POI",
                    state = poiLayerVisible,
                    enabled = !distractingElementsEnabled.value,
                )
            }
        }
    }
}

private fun getTransitLayer(visible: Boolean) = DashMapStyleLayer(id = TRANSIT_LAYER_ID, visible = visible)
private fun getPoiLayer(visible: Boolean) = DashMapStyleLayer(id = POI_LAYER_ID, visible = visible)
