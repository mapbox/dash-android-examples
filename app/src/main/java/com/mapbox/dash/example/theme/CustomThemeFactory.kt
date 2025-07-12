package com.mapbox.dash.example.theme

import com.mapbox.dash.example.R
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import com.mapbox.dash.sdk.config.api.ThemeFactory
import com.mapbox.dash.theming.BorderColor
import com.mapbox.dash.theming.ButtonColor
import com.mapbox.dash.theming.DashColors
import com.mapbox.dash.theming.EvRangeMapStyles
import com.mapbox.dash.theming.GuidanceColor
import com.mapbox.dash.theming.IconColor
import com.mapbox.dash.theming.RouteLineColors
import com.mapbox.dash.theming.TextColor
import com.mapbox.dash.theming.Theme
import com.mapbox.dash.theming.icons.DashIconCollection
import com.mapbox.dash.theming.icons.MainIconSet
import com.mapbox.dash.theming.icons.DashIcon

internal object CustomThemeFactory : ThemeFactory {

    override fun createTheme(context: Context, isNightTheme: Boolean): Theme {
        val accentColor = Color(ContextCompat.getColor(context, R.color.accent))
        val colors = DashColors(
            isNightTheme,
            textColor = TextColor(isNightTheme, accent = accentColor, links = accentColor),
            buttonColors = ButtonColor(isNightTheme, primary = accentColor),
            borderColors = BorderColor(isNightTheme, accent = accentColor),
            iconColor = IconColor(isNightTheme, accent = accentColor),
            guidanceColor = GuidanceColor(isNightTheme, primary = accentColor),
        )
        return Theme(
            context, if (isNightTheme) "custom-night" else "custom-day", isNightTheme, colors,
            evRangeMapStyles = EvRangeMapStyles(
                isNightTheme,
                floodLightColorNormal = accentColor.toArgb(),
                borderColorNormal = accentColor.toArgb(),
            ),
            iconCollection = DashIconCollection(
                colors, context,
                main = MainIconSet(
                    colors, context,
                    coffee = DashIcon(context, R.drawable.ic_pixel_coffee),
                    favorite = DashIcon(context, R.drawable.ic_pixel_favorite),
                    food = DashIcon(context, R.drawable.ic_pixel_food),
                    fuel = DashIcon(context, R.drawable.ic_pixel_fuel),
                    home = DashIcon(context, R.drawable.ic_pixel_home),
                    parking = DashIcon(context, R.drawable.ic_pixel_parking),
                    recents = DashIcon(context, R.drawable.ic_pixel_recents),
                    work = DashIcon(context, R.drawable.ic_pixel_work),
                ),
            ),
            routeLineColors = RouteLineColors(
                isNightTheme,
                routeCasingColor = if (isNightTheme) android.graphics.Color.WHITE else android.graphics.Color.BLACK,
                alternativeRouteCasingColor = 0xFF888888.toInt(),
                inactiveRouteLegCasingColor = 0xFF888888.toInt(),
            ),
        )
    }
}
