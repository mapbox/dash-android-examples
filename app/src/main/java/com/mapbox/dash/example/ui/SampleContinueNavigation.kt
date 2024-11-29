@file:SuppressWarnings("MagicNumber")

package com.mapbox.dash.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mapbox.dash.driver.R
import com.mapbox.dash.driver.presentation.waypoint.ContinueNavigationUiState

@Composable
fun SampleContinueNavigation(
    modifier: Modifier = Modifier,
    state: ContinueNavigationUiState,
) {
    Row(
        modifier = modifier
            .width(540.dp)
            .background(Color(0xFF018786), shape = RoundedCornerShape(24.dp))
            .padding(vertical = 16.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.dash_waypoint_continue_navigation),
            color = Color.White,
        )
        Text(
            modifier = Modifier
                .background(Color(0xFF3072F5), shape = RoundedCornerShape(16.dp))
                .padding(vertical = 32.dp, horizontal = 16.dp)
                .clickable(onClick = state.onClickContinueNavigation),
            text = stringResource(id = R.string.dash_waypoint_continue_navigation_text),
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}
