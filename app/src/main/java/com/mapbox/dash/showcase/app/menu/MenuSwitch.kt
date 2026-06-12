package com.mapbox.dash.showcase.app.menu

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.mapbox.dash.sdk.DashNavigationFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull

private const val SCALE = 5f / 6f

@Composable
fun MenuSwitch(
    text: String,
    initial: Boolean,
    dashNavigationFragmentFlow: Flow<DashNavigationFragment?>,
    onCheckedChange: (DashNavigationFragment, Boolean) -> Unit,
) {
    MenuSwitch(
        text = text,
        initial = initial,
        onCheckedChange = { checked ->
            dashNavigationFragmentFlow.filterNotNull().collect { dashNavigationFragment ->
                onCheckedChange(dashNavigationFragment, checked)
            }
        },
    )
}

@Composable
fun MenuSwitch(
    text: String,
    initial: Boolean,
    onCheckedChange: suspend (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    val state = rememberSaveable(initial) { mutableStateOf(initial) }
    LaunchedEffect(state.value) {
        onCheckedChange(state.value)
    }
    MenuSwitch(
        text = text,
        state = state,
        enabled = enabled,
    )
}

@Composable
fun <T> MenuSwitch(
    text: String,
    flow: StateFlow<T>,
    transform: (T) -> Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    MenuSwitch(
        text = text,
        checked = transform(flow.collectAsState().value),
        onCheckedChange = onCheckedChange,
    )
}

@Composable
fun MenuSwitch(
    text: String,
    state: MutableState<Boolean>,
    enabled: Boolean = true,
) {
    MenuSwitch(
        text = text,
        checked = state.value,
        onCheckedChange = { state.value = it },
        enabled = enabled,
    )
}

@Composable
private fun MenuSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
        )
        Switch(
            modifier = Modifier.graphicsLayer(
                scaleX = SCALE, scaleY = SCALE,
                transformOrigin = TransformOrigin(pivotFractionX = 1f, pivotFractionY = 0.5f),
            ),
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange,
        )
    }
}
