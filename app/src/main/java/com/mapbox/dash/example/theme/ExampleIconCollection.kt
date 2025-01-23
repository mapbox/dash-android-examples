package com.mapbox.dash.example.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import com.mapbox.dash.theming.R

@Stable
data class ShowcaseIconCollection(
    val controls: ControlsIconSet = ControlsIconSet(),
    val main: MainIconSet = MainIconSet(),
)

data class ControlsIconSet(
    @DrawableRes val cross: Int = R.drawable.ic_navux_controls_cross,
    @DrawableRes val longArrowLeft: Int = R.drawable.ic_navux_controls_long_arrow_left,
    @DrawableRes val arrowUp: Int = R.drawable.ic_navux_controls_arrow_up,
    @DrawableRes val edit: Int = R.drawable.ic_navux_controls_edit,
)

data class MainIconSet(
    @DrawableRes val charging: Int = R.drawable.ic_navux_main_charging,
)
