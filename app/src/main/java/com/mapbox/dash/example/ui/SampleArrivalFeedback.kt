@file:SuppressWarnings("MagicNumber")

package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mapbox.dash.driver.presentation.ArrivalFeedbackUiState
import com.mapbox.dash.sdk.feedback.R

@Composable
fun SampleArrivalFeedback(
    modifier: Modifier,
    state: ArrivalFeedbackUiState,
) {
    Row(
        modifier
            .width(540.dp)
            .background(Color(0xFF181B20), shape = RoundedCornerShape(8.dp))
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp),
        ) {
            Text(text = stringResource(id = R.string.dash_arrival_feedback_text), color = Color.White)
            Text(text = state.destination.name, color = Color.White.copy(alpha = 0.6f))
        }

        Image(
            modifier = Modifier
                .padding(end = 16.dp)
                .width(90.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF3072F5))
                .clickable(onClick = state.onClickGoodTripButton)
                .padding(all = 8.dp),
            imageVector = Icons.Default.ThumbUp,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null,
        )
        Image(
            modifier = Modifier
                .padding(end = 16.dp)
                .width(90.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEB252A))
                .clickable(onClick = state.onClickBadTripButton)
                .padding(all = 8.dp)
                .rotate(degrees = 180f),
            imageVector = Icons.Default.ThumbUp,
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = null,
        )
    }
}
