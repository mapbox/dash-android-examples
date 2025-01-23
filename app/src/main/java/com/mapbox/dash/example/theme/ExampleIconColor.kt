package com.mapbox.dash.example.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.mapbox.dash.theming.R

@Stable
data class ExampleIconColor(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val inverted: Color,
    val red: Color,
    val green: Color,
    val accent: Color,
) {

    internal object ColorModeProvider {

        @Composable
        @ReadOnlyComposable
        fun get() = ExampleIconColor(
            primary = colorResource(id = R.color.icon_color_primary_day),
            secondary = colorResource(id = R.color.icon_color_secondary_day),
            tertiary = colorResource(id = R.color.icon_color_tertiary_day),
            inverted = colorResource(id = R.color.icon_color_inverted_day),
            red = colorResource(id = R.color.icon_color_red_day),
            green = colorResource(id = R.color.icon_color_green_day),
            accent = colorResource(id = R.color.icon_color_accent_day),
        )
    }
}
