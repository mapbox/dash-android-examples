package com.mapbox.dash.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.destination.preview.presentation.compose.OfflineRouteAlertState
import com.mapbox.dash.example.R

@Composable
fun SampleOfflineRouteAlert(modifier: Modifier, state: OfflineRouteAlertState) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = state.onDismissed,
            )
            .systemBarsPadding()
            .padding(horizontal = 144.dp, vertical = 24.dp)
            .background(Color(red = 16, green = 18, blue = 23))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {},
            ),
    ) {
        AlertHeader(state = state)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.2f)),
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 88.dp, top = 112.dp, end = 88.dp),
            text = "Route may not be best. The route and traffic will be automatically updated when you’re back online",
            fontSize = 42.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
        )
        AlertButtons(state = state)
    }
}

@Composable
private fun AlertHeader(state: OfflineRouteAlertState) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 24.dp),
            text = "No network",
            fontSize = 51.sp,
            lineHeight = 72.sp,
            fontWeight = FontWeight.Light,
            color = Color.White,
        )
        Image(
            modifier = Modifier
                .size(128.dp)
                .clickable(onClick = state.onDismissed)
                .padding(all = 28.dp),
            painter = painterResource(id = R.drawable.ic_alert_close),
            contentDescription = null,
        )
    }
}

@Composable
private fun AlertButtons(state: OfflineRouteAlertState) {
    Row(
        modifier = Modifier
            .padding(start = 88.dp, end = 88.dp, bottom = 32.dp)
            .widthIn(max = 1048.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .height(112.dp)
                .background(color = Color.White.copy(alpha = 0.1f))
                .clickable(onClick = state.onDismissed)
                .wrapContentSize(),
            text = "Cancel",
            fontSize = 42.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .height(112.dp)
                .background(color = Color.White)
                .clickable(onClick = state.onNavigateClicked)
                .wrapContentSize(),
            text = "Start driving",
            fontSize = 42.sp,
            lineHeight = 56.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Black,
        )
    }
}
