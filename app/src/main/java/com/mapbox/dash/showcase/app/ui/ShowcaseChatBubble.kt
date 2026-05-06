package com.mapbox.dash.showcase.app.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.mapbox.dash.showcase.app.R
import com.mapbox.dash.showcase.app.theme.SampleColors
import com.mapbox.navigation.mapgpt.MapGptCompose
import com.mapbox.navigation.mapgpt.ui.ChatBubbleState

@Suppress("MagicNumber")
@Composable
fun ShowcaseChatBubble(
    modifier: Modifier = Modifier,
    state: ChatBubbleState = ChatBubbleState.Idle,
) {
    val isDarkTheme = isSystemInDarkTheme()
    val customPrimaryText = if (isDarkTheme) Color(0xFFD0D4DA) else Color(0xFF1A1D21)
    val customBackground = if (isDarkTheme) Color(0xAA121418) else Color(0xAAF7F8FA)
    val text = when (state) {
        is ChatBubbleState.StringBubble -> state.text
        is ChatBubbleState.ResourceBubble -> stringResource(id = state.stringId)
        else -> stringResource(id = R.string.showcase_custom_mapgpt_chat_bubble_idle_state)
    }
    val defaultMargin = dimensionResource(id = com.mapbox.dash.theming.R.dimen.default_margin)
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth()
            .padding(horizontal = defaultMargin, vertical = defaultMargin / 2)
            .height(dimensionResource(id = com.mapbox.map.gpt.R.dimen.mapgpt_bubble_height))
            .background(customBackground)
            .animateContentSize()
            .padding(horizontal = defaultMargin)
            .wrapContentHeight(),
        color = when (state.source) {
            ChatBubbleState.Source.USER -> customPrimaryText
            ChatBubbleState.Source.AI -> SampleColors.primary
            ChatBubbleState.Source.ERROR -> SampleColors.error
            ChatBubbleState.Source.WARNING -> SampleColors.primary
            else -> {
                Log.w("MapGptChatBubble", "Unknown source: ${state.source}")
                SampleColors.primary
            }
        },
        fontSize = 28.sp,
        fontWeight = FontWeight.Normal,
    )
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
internal fun Preview_ShowcaseCustomChatBubble() = MapGptCompose().PreviewTheme {
    ShowcaseChatBubble()
}
