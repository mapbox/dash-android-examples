package com.mapbox.dash.example.theme

import android.content.Context
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mapbox.dash.example.R
import com.mapbox.dash.sdk.config.api.ThemeFactory
import com.mapbox.dash.theming.BorderColor
import com.mapbox.dash.theming.ButtonColor
import com.mapbox.dash.theming.DashColors
import com.mapbox.dash.theming.EvRangeMapStyles
import com.mapbox.dash.theming.GuidanceColor
import com.mapbox.dash.theming.IconColor
import com.mapbox.dash.theming.PinColors
import com.mapbox.dash.theming.Shapes
import com.mapbox.dash.theming.TextColor
import com.mapbox.dash.theming.Theme

internal object RedThemeFactory : ThemeFactory {

    override fun createTheme(context: Context, isNightTheme: Boolean): Theme {
        val redColor = Color(ContextCompat.getColor(context, R.color.color_red))
        val colors = DashColors(
            isNightTheme,
            textColor = TextColor(isNightTheme, accent = redColor, links = redColor),
            buttonColors = ButtonColor(isNightTheme, primary = redColor),
            borderColors = BorderColor(isNightTheme, accent = redColor),
            iconColor = IconColor(isNightTheme, accent = redColor),
            guidanceColor = GuidanceColor(isNightTheme, primary = redColor),
            pinColors = PinColors(isNightTheme, search = redColor),
        )
        return Theme(
            context, if (isNightTheme) "red-night" else "red-day", isNightTheme, colors,
            evRangeMapStyles = EvRangeMapStyles(
                isNightTheme,
                floodLightColorNormal = redColor.toArgb(),
                borderColorNormal = redColor.toArgb(),
            ),
            shapes = Shapes(
                actionButton = CircleShape,
                searchPanel = CircleShape,
                etaPanel = CircleShape,
                endNavigation = CircleShape,
                resumeGuidance = RoundedCornerShape(24.dp),
                poiCard = RoundedCornerShape(4.dp),
                settings = RoundedCornerShape(4.dp),
                guidanceBanner = RoundedCornerShape(4.dp),
                maneuversList = RoundedCornerShape(4.dp),
                rateTripPanel = RoundedCornerShape(4.dp),
                continueNavigationPanel = RoundedCornerShape(4.dp),
                streetName = CircleShape,
            ),
        )
    }
}