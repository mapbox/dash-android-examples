package com.mapbox.dash.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.dash.driver.presentation.searcharea.SearchAreaUiState

@Composable
fun SampleSearchArea(uiState: SearchAreaUiState) {
    SearchArea(uiState.onClick)
}

@Composable
private fun SearchArea(onClick: () -> Unit) {
    Text(
        text = "Search here",
        modifier = Modifier
            .border(1.dp, Color.Blue)
            .background(Color.White)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp,
            )
            .clickable(onClick = onClick),
        fontWeight = FontWeight.Bold,
        color = Color.Black,
    )
}

@Preview(name = "Light_Tablet", device = Devices.TABLET)
@Composable
internal fun Preview_SearchArea() {
    MaterialTheme {
        SearchArea(onClick = {})
    }
}
