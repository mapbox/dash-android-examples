package com.mapbox.dash.showcase.app.menu

import androidx.annotation.ColorRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: suspend () -> Unit,
    @ColorRes backgroundId: Int = android.R.color.holo_blue_dark,
) {
    val progress = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val backgroundColor = colorResource(backgroundId)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(4.dp, RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable(!progress.value) {
                coroutineScope.launch {
                    try {
                        progress.value = true
                        onClick()
                    } finally {
                        progress.value = false
                    }
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(8.dp),
            text = text,
            color = if (progress.value) backgroundColor else Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        if (progress.value) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
            )
        }
    }
}
