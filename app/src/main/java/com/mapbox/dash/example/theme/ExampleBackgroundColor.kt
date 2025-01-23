package com.mapbox.dash.example.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.mapbox.dash.theming.R

@Stable
data class ExampleBackgroundColor(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val quaternary: Color,
    val fade: Color,
    val sidebar: Color,
) {

    internal object ColorModeProvider {

        @Composable
        @ReadOnlyComposable
        fun get() = ExampleBackgroundColor(
            primary = colorResource(id = R.color.background_color_primary_day),
            secondary = colorResource(id = R.color.background_color_secondary_day),
            tertiary = colorResource(id = R.color.background_color_tertiary_day),
            quaternary = colorResource(id = R.color.background_color_quaternary_day),
            fade = colorResource(id = R.color.background_color_fade_day),
            sidebar = colorResource(id = R.color.background_color_sidebar_day),
        )
    }
}
