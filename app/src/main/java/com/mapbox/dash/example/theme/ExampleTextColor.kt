package com.mapbox.dash.example.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.mapbox.dash.theming.R

@Stable
data class ExampleTextColor(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val links: Color,
    val accent: Color,
    val inverted: Color,
    val red: Color,
    val green: Color,
) {

    internal object ColorModeProvider {

        @Composable
        @ReadOnlyComposable
        fun get() = ExampleTextColor(
            primary = colorResource(id = R.color.text_color_primary_day),
            secondary = colorResource(id = R.color.text_color_secondary_day),
            tertiary = colorResource(id = R.color.text_color_tertiary_day),
            links = colorResource(id = R.color.text_color_links_day),
            accent = colorResource(id = R.color.text_color_accent_day),
            inverted = colorResource(id = R.color.text_color_inverted_day),
            red = colorResource(id = R.color.text_color_red_day),
            green = colorResource(id = R.color.text_color_green_day),
        )
    }
}
