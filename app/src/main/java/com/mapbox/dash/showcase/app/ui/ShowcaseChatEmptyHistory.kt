package com.mapbox.dash.showcase.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.navigation.mapgpt.MapGptCompose

@Composable
internal fun BoxScope.ShowcaseChatEmptyHistory() {
    Text(
        text = "You haven't started any conversations yet",
        color = Color.LightGray,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(280.dp)
            .align(Alignment.Center),
    )
}

@Preview
@Composable
internal fun Preview_ShowcaseChatEmptyHistory() = MapGptCompose().PreviewTheme {
    Box {
        ShowcaseChatEmptyHistory()
    }
}
