package com.mapbox.dash.showcase.app.menu

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuEditText(
    state: MutableState<String>,
    hint: String,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        value = state.value,
        onValueChange = { state.value = it },
        singleLine = true,
        placeholder = { Text(hint) },
    )
}
