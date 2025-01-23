package com.mapbox.dash.example.theme

import androidx.annotation.DimenRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.TextUnit
import com.mapbox.dash.theming.R

@Stable
data class ExampleTypography(
    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val title4: TextStyle,
    val title5: TextStyle,
    val title6: TextStyle,
    val title7: TextStyle,
    val title8: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val body4: TextStyle,
    val body5: TextStyle,
    val body6: TextStyle,
    val button1: TextStyle,
    val button2: TextStyle,
) {

    internal object StyleProvider {

        @Composable
        @ReadOnlyComposable
        private fun fontDimensionResource(@DimenRes id: Int): TextUnit {
            return with(LocalDensity.current) { dimensionResource(id).toSp() }
        }

        @Composable
        @ReadOnlyComposable
        @SuppressWarnings("LongMethod")
        internal fun get() = ExampleTypography(
            title1 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title1_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title1_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            title2 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title2_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title2_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            title3 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title3_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title3_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            title4 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title4_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title4_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            title5 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title5_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title5_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            title6 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title6_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title6_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            title7 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title7_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title7_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            title8 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.title8_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.title8_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            body1 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif"),
                    weight = FontWeight.Normal,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.body1_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.body1_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            body2 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif"),
                    weight = FontWeight.Normal,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.body2_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.body2_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            body3 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif"),
                    weight = FontWeight.Normal,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.body3_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.body3_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            body4 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif"),
                    weight = FontWeight.Normal,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.body4_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.body4_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            body5 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif"),
                    weight = FontWeight.Normal,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.body5_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.body5_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            body6 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif"),
                    weight = FontWeight.Normal,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.body6_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.body6_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            button1 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.button1_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.button1_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
            button2 = TextStyle(
                fontFamily = Font(
                    DeviceFontFamilyName("sans-serif-medium"),
                    weight = FontWeight.SemiBold,
                ).toFontFamily(),
                fontSize = fontDimensionResource(id = R.dimen.button2_font_size),
                lineHeight = fontDimensionResource(id = R.dimen.button2_line_height),
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None),
                textMotion = TextMotion.Animated,
            ),
        )
    }
}
