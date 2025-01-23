package com.mapbox.dash.example.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.mapbox.dash.theming.R

@Stable
data class ExampleButtonColor(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val quaternary: Color,
    val transparent: Color,
    val red: Color,
    val secondaryNew: Color,
    val secondaryProgress: Color,
) {

    internal object ColorModeProvider {

        @Composable
        @ReadOnlyComposable
        fun get() = ExampleButtonColor(
            primary = colorResource(id = R.color.button_color_primary_day),
            secondary = colorResource(id = R.color.button_color_secondary_day),
            tertiary = colorResource(id = R.color.button_color_tertiary_day),
            quaternary = colorResource(id = R.color.button_color_quaternary_day),
            transparent = Color.Transparent,
            red = colorResource(id = R.color.button_color_red_day),
            secondaryNew = colorResource(id = R.color.button_color_secondary_new_day),
            secondaryProgress = colorResource(id = R.color.button_color_secondary_progress_day),
        )
    }
}
