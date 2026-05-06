package com.mapbox.dash.showcase.app.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.navigation.mapgpt.MapGptCompose
import com.mapbox.navigation.mapgpt.ui.screen.ChatItem
import kotlinx.coroutines.launch

@Composable
fun ShowcaseChat(
    modifier: Modifier = Modifier,
    mapGptCompose: MapGptCompose,
    onCloseClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val chatItems = mapGptCompose.chatItems.collectAsState().value
    val onSendClick: (String) -> Unit = { message ->
        coroutineScope.launch { mapGptCompose.onSendClicked(message) }
    }
    val onCardClick: (ChatItem.Card) -> Unit = { card -> mapGptCompose.onCardClicked(card) }

    ShowcaseChatContent(
        modifier = modifier,
        onCloseClick = onCloseClick,
        chatItems = chatItems,
        onSendClick = onSendClick,
        onCardClick = onCardClick,
    )
}

@Composable
private fun ShowcaseChatContent(
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    chatItems: List<ChatItem> = emptyList(),
    onSendClick: (String) -> Unit = {},
    onCardClick: (ChatItem.Card) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val message = remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val listState = rememberLazyListState()

        Box(
            modifier = Modifier.weight(1f)
                .fillMaxWidth(),
        ) {
            if (chatItems.isEmpty()) {
                ShowcaseChatEmptyHistory()
            }
            ShowcaseChatMessages(chatItems, listState, onCardClick)
            CloseButton(onCloseClick)
        }
        Box {
            ShowcaseChatMessageBox(message) { text ->
                scope.launch { listState.animateScrollToItem(index = 0) }
                onSendClick(text)
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color.Black),
            )
        }
    }
}

@Composable
private fun BoxScope.CloseButton(
    onCloseClick: () -> Unit,
) {
    Image(
        modifier = Modifier
            .size(40.dp)
            .align(Alignment.TopStart)
            .clip(shape = RoundedCornerShape(12.dp))
            .background(color = Color.LightGray)
            .clickable(onClick = onCloseClick)
            .padding(all = 8.dp),
        painter = painterResource(id = android.R.drawable.ic_notification_clear_all),
        contentDescription = null,
    )
}

@Preview
@Composable
fun Preview_ShowcaseChat() = MapGptCompose().PreviewTheme {
    ShowcaseChatContent(
        chatItems = listOf(),
    )
}
