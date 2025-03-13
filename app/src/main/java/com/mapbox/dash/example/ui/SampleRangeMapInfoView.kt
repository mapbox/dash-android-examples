package com.mapbox.dash.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.sdk.ev.domain.model.RangeMapInfoUiState

@Composable
fun SampleRangeMapInfoView(modifier: Modifier, state: RangeMapInfoUiState) {
    Column(
        modifier = modifier
            .width(448.dp)
            .background(Color(red = 16, green = 18, blue = 23), RoundedCornerShape(8.dp))
            .padding(top = 12.dp, bottom = 28.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Charge percentage: ${state.chargePercentage}",
            fontSize = 36.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Range meters: ${state.rangeMeters}",
            fontSize = 36.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Low Battery: ${state.lowBattery}",
            fontSize = 36.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
        )
    }
}
