package com.mapbox.dash.example.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

object ExampleAppTheme {

    val colors: ExampleColors
        @Composable
        @ReadOnlyComposable
        get() = ExampleColors(
            textColor = ExampleTextColor.ColorModeProvider.get(),
            backgroundColors = ExampleBackgroundColor.ColorModeProvider.get(),
            buttonColors = ExampleButtonColor.ColorModeProvider.get(),
            iconColor = ExampleIconColor.ColorModeProvider.get(),
        )

    val icons: ShowcaseIconCollection
        get() = ShowcaseIconCollection()

    val shapes: ShowcaseShapes
        @Composable
        @ReadOnlyComposable
        get() = ShowcaseShapes.create()

    val typography: ExampleTypography
        @Composable
        @ReadOnlyComposable
        get() = ExampleTypography.StyleProvider.get()
}
