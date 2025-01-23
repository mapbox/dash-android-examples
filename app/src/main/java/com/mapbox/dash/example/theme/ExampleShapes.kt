package com.mapbox.dash.example.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.mapbox.dash.theming.R

@Stable
data class ShowcaseShapes(
    val actionButtonBackground: Shape,
    val searchPanelBackground: Shape,
    val poiCardBackground: Shape,
    val driverNotificationBackground: Shape,
    val driverNotificationButtonBackground: Shape,
) {
    companion object {

        @Composable
        @ReadOnlyComposable
        fun create(): ShowcaseShapes {
            val radius = dimensionResource(
                id = if (isTablet()) R.dimen.shapes_extra_large_radius
                else R.dimen.shapes_large_radius,
            )

            return ShowcaseShapes(
                actionButtonBackground = RoundedCornerShape(radius),
                searchPanelBackground = RoundedCornerShape(radius),
                poiCardBackground = RoundedCornerShape(
                    topStart = radius,
                    topEnd = radius,
                    bottomStart = if (isTablet()) radius else 0.dp,
                    bottomEnd = if (isTablet()) radius else 0.dp,
                ),
                driverNotificationBackground = RoundedCornerShape(radius),
                driverNotificationButtonBackground = RoundedCornerShape(radius),
            )
        }
    }
}

@Composable
@ReadOnlyComposable
fun isTablet(): Boolean {
    return booleanResource(id = R.bool.is_tablet)
}
