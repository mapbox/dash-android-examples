package com.mapbox.dash.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.dash.driver.presentation.streetname.StreetNameUiState

@Composable
fun SampleStreetName(
    modifier: Modifier = Modifier,
    uiState: StreetNameUiState,
) {
    StreetNameView(modifier, uiState.text, uiState.inlineContent)
}

@Composable
private fun StreetNameView(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    inlineContent: Map<String, InlineTextContent>,
) {
    Text(
        text = text,
        modifier = modifier
            .widthIn(max = 300.dp)
            .border(1.dp, Color.Blue)
            .background(Color.White)
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp,
            ),
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        inlineContent = inlineContent,
    )
}

@Preview(name = "Light_Tablet", device = Devices.TABLET)
@Composable
internal fun Preview_StreetNameView() {
    MaterialTheme {
        StreetNameView(
            text = AnnotatedString(text = "East Anaheim Street"),
            inlineContent = emptyMap(),
        )
    }
}
