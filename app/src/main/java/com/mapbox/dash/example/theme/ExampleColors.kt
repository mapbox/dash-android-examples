package com.mapbox.dash.example.theme

import androidx.compose.runtime.Stable

@Stable
data class ExampleColors(
    val textColor: ExampleTextColor,
    val backgroundColors: ExampleBackgroundColor,
    val buttonColors: ExampleButtonColor,
    val iconColor: ExampleIconColor,
)
