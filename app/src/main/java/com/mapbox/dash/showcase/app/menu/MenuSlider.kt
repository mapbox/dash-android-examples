package com.mapbox.dash.showcase.app.menu

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuSlider(
    state: MutableState<Float>,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
) {
    Row(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = label,
        )
        Slider(
            modifier = Modifier.weight(1f),
            value = state.value,
            onValueChange = { state.value = it },
            valueRange = valueRange,
        )
    }
}
