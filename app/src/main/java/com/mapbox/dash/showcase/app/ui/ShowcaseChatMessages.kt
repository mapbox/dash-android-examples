@file:Suppress("MagicNumber")

package com.mapbox.dash.showcase.app.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mapbox.map.gpt.R
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.magpt.ui.cards.CardOptions
import com.mapbox.navigation.magpt.ui.cards.GenericCardView
import com.mapbox.navigation.mapgpt.MapGptCompose
import com.mapbox.navigation.mapgpt.core.api.SessionFrame
import com.mapbox.navigation.mapgpt.ui.screen.ChatItem

@Composable
internal fun ShowcaseChatMessages(
    chatItems: List<ChatItem>,
    listState: LazyListState = rememberLazyListState(),
    onCardClick: (ChatItem.Card) -> Unit = {},
) {
    ChatList(
        listState = listState,
        chatItems = chatItems,
        onCardClick = onCardClick,
    )
}

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
@Composable
private fun ChatList(
    listState: LazyListState,
    chatItems: List<ChatItem>,
    onCardClick: (ChatItem.Card) -> Unit = {},
) {
    LazyColumn(
        reverseLayout = true,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom),
        contentPadding = PaddingValues(
            top = 80.dp,
            bottom = 0.dp,
        ),
        state = listState,
    ) {
        item(key = "footer") {} // keeps the Chat scrolled to bottom
        items(chatItems, key = { it.itemId }) { chatItem ->
            val cornerRadius = 16.dp
            when (chatItem) {
                is ChatItem.User -> ChatItemBubble(
                    text = chatItem.message,
                    align = Alignment.End,
                    color = Color(0xFF33D158),
                    shape = RoundedCornerShape(cornerRadius, cornerRadius, 4.dp, cornerRadius),
                )

                is ChatItem.AI -> ChatAiItemBubble(chatItem)

                ChatItem.Processing -> CircularProgressIndicator(
                    modifier = Modifier.width(32.dp),
                    color = Color.White,
                )

                is ChatItem.Card -> {
                    ChatCardView(
                        card = chatItem.card,
                        onClick = { onCardClick(chatItem) },
                    )
                }
            }
        }
    }
}

@ExperimentalPreviewMapboxNavigationAPI
@Composable
private fun ChatCardView(
    card: SessionFrame.SendEvent.Body.Entity.Data.Card,
    onClick: () -> Unit,
    isDarkMode: Boolean = true,
) {
    val configuration = CardOptions(
        isDarkMode = isDarkMode,
        isTablet = isTablet(),
        enableAnchoredIcon = true,
    )
    GenericCardView(
        card = card, configuration = configuration,
        modifier = Modifier
            .height(72.dp)
            .widthIn(max = Dp.Unspecified)
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF26252A), shape = RoundedCornerShape(16.dp))
            .padding(all = 8.dp),
    )
}

@Composable
private fun ChatItemBubble(
    text: String,
    fullText: CharSequence = text,
    align: Alignment.Horizontal,
    color: Color,
    shape: Shape,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = Modifier
            .testTag(fullText.toString())
            .fillMaxWidth()
            .wrapContentWidth(align)
            .maxChatItemWidth(align)
            .background(color, shape)
            .padding(all = 12.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.animateContentSize(),
            color = Color.White,
        )
        content()
    }
}

@Composable
private fun ChatAiItemBubble(
    chatItem: ChatItem.AI,
) {
    ChatItemBubble(
        text = chatItem.message,
        fullText = chatItem.message,
        align = Alignment.Start,
        color = Color.Black,
        shape = aiBubbleShape(),
    )
}

@Composable
private fun aiBubbleShape(): Shape {
    val cornerRadius = dimensionResource(id = R.dimen.mapgpt_rounded_corner_radius)
    return RoundedCornerShape(cornerRadius, cornerRadius, cornerRadius, 4.dp)
}

private fun Modifier.maxChatItemWidth(align: Alignment.Horizontal): Modifier = composed {
    if (isTablet()) {
        fillMaxWidth(fraction = 0.6f).wrapContentWidth(align)
    } else {
        widthIn(max = 320.dp)
    }
}

@Composable
@ReadOnlyComposable
private fun isTablet(): Boolean {
    return booleanResource(id = R.bool.is_tablet)
}

@Preview
@Composable
internal fun Preview_ShowcaseChatMessages() = MapGptCompose().PreviewTheme {
    val chatItems = listOf(
        ChatItem.Processing,
    )
    ShowcaseChatMessages(chatItems = chatItems)
}
