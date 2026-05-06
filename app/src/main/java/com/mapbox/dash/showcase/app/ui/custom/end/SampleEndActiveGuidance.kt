@file:Suppress("MagicNumber")

package com.mapbox.dash.showcase.app.ui.custom.end

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.driver.presentation.end.EndActiveGuidanceUiState

@Composable
fun SampleEndActiveGuidance(
    modifier: Modifier,
    state: EndActiveGuidanceUiState,
) {
    Text(
        text = "End route",
        color = Color.White,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        modifier = modifier
            .fillMaxWidth()
            .padding(end = dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin))
            .height(dimensionResource(id = com.mapbox.dash.theming.R.dimen.button_height))
            .clip(shape = CutCornerShape(16.dp))
            .background(Color(0xFF3072F5))
            .clickable(onClick = state.onEndActiveGuidanceClick)
            .padding(dimensionResource(com.mapbox.dash.theming.R.dimen.default_margin))
            .wrapContentSize(),
    )
}
