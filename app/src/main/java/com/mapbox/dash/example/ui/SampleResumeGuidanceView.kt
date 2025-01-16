package com.mapbox.dash.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.route.restore.ResumeGuidanceViewState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SampleResumeGuidanceView(modifier: Modifier, state: ResumeGuidanceViewState) {
    LaunchedEffect(Unit) {
        delay(10.seconds)
        state.onDeclineClick()
    }
    Column(
        modifier = modifier
            .width(448.dp)
            .background(Color(red = 16, green = 18, blue = 23), RoundedCornerShape(8.dp))
            .padding(top = 12.dp, bottom = 28.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Continue route to",
            fontSize = 36.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.places.last().name,
            fontSize = 32.sp,
            lineHeight = 40.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 32.dp, end = 32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(red = 0, green = 122, blue = 255))
                .clickable(onClick = state.onAcceptClick)
                .padding(vertical = 24.dp),
            text = "Accept",
            fontSize = 42.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}
