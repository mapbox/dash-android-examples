package com.mapbox.dash.showcase.app

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.showcase.app.ui.ShowcaseChat
import com.mapbox.dash.showcase.app.ui.ShowcaseChatBubble
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.magpt.ui.cards.CardOptions
import com.mapbox.navigation.magpt.ui.cards.GenericCardView
import com.mapbox.navigation.mapgpt.MapGptCompose
import com.mapbox.navigation.mapgpt.stopMapGptConversation
import com.mapbox.navigation.mapgpt.useroutput.PrebuiltMapGptAvatars

/**
 * This showcases customizing the MapGPT UI beyond the configurations available in the SDK.
 *
 * This approach is needed when repositioning existing components, or adding new components
 * so that MapGPT becomes more integrated into your application's UI.
 */
@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
@Suppress("MagicNumber")
@Composable
fun CustomMapGptUI(
    mapGptCompose: MapGptCompose = MapGptCompose(),
    showCustomChatBubble: Boolean,
) {
    // When using DashNavigationFragment, MapGpt is attached to the Lifecycle of the Fragment.
    // When creating a custom UI that does not use DashNavigationFragment, you need to manage
    // the lifecycle of MapGpt yourself. This is done by with Connection().
    mapGptCompose.Connection()

    // When building custom UIs, there are configurations available to show or hide the
    // components built into the MapGpt UI. This is done by using the DashMapGptConfig.
    DisposableEffect(mapGptCompose, showCustomChatBubble) {
        val mapGptConfig = mapGptCompose.config.value
        mapGptCompose.config.value = mapGptConfig.build {
            showCarousel = true
            showChatBubble = !showCustomChatBubble
            showKeyboardMode = false
        }
        onDispose {
            mapGptCompose.config.value = mapGptCompose.config.value.build {
                showCarousel = mapGptConfig.showCarousel
                showChatBubble = mapGptConfig.showChatBubble
                showKeyboardMode = mapGptConfig.showKeyboardMode
            }
        }
    }

    val keyboardModeExpanded: MutableState<Boolean> = remember { mutableStateOf(false) }
    if (keyboardModeExpanded.value) {
        CustomKeyboardModeExpanded(
            mapGptCompose = mapGptCompose,
            onCloseClick = { keyboardModeExpanded.value = false },
        )
    } else {
        CustomKeyboardModeCollapsed(
            mapGptCompose = mapGptCompose,
            showCustomChatBubble = showCustomChatBubble,
            onExpandClick = { keyboardModeExpanded.value = true },
        )
    }
    val config by mapGptCompose.config.collectAsState()
    val carouselItems by mapGptCompose.carouselItems.collectAsState()

    if (!config.showCarousel && carouselItems.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .systemBarsPadding()
                .imePadding()
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(start = 48.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                IconButton(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black),
                    onClick = { Dash.controller.stopMapGptConversation() },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                    )
                }
            }
            items(carouselItems) { item ->
                GenericCardView(
                    card = item.card,
                    configuration = CardOptions(
                        isDarkMode = false,
                        isTablet = true,
                        enableAnchoredIcon = false,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(if (isSystemInDarkTheme()) Color(0xAA121418) else Color(0xAAF7F8FA)),
                )
            }
        }
    }
}

@Composable
fun CustomKeyboardModeExpanded(
    mapGptCompose: MapGptCompose,
    onCloseClick: () -> Unit = {},
) {
    ShowcaseChat(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        mapGptCompose = mapGptCompose,
        onCloseClick = onCloseClick,
    )
}

@Composable
fun CustomKeyboardModeCollapsed(
    mapGptCompose: MapGptCompose,
    showCustomChatBubble: Boolean,
    onExpandClick: () -> Unit = {},
) {
    val mapGptComposeConfig by mapGptCompose.config.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding()
            .padding(16.dp),
    ) {
        if (mapGptComposeConfig.showKeyboardMode) {
            mapGptCompose.KeyboardModeButton(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                onClick = onExpandClick,
            )
        }

        // Column to stack ChatBubbleView above the Avatar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // The conversation carousel will show the chat bubble view if enabled by MapGptConfig.
            if (mapGptComposeConfig.showCarousel) {
                mapGptCompose.ChatCarousel()
            }

            // Showcase the ability to change the ChatBubbleView or use the default
            // in a specific location.
            if (showCustomChatBubble) {
                val chatBubbleState = mapGptCompose.chatBubbleState.collectAsState().value
                ShowcaseChatBubble(state = chatBubbleState)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Avatar at the bottom and using the Petter avatar for customization example.
            @SuppressLint("RestrictToUsage", "RestrictedApi")
            if (mapGptComposeConfig.showAvatar) {
                mapGptCompose.Avatar(
                    onClick = mapGptCompose::onActivationClicked,
                    mapGptAvatar = PrebuiltMapGptAvatars.petterAvatar,
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview_CustomKeyboardModeExpanded() {
    val mapGptCompose = MapGptCompose()
    mapGptCompose.PreviewTheme {
        CustomKeyboardModeExpanded(mapGptCompose = mapGptCompose)
    }
}

@Preview
@Composable
fun Preview_CustomKeyboardModeCollapsed() {
    val mapGptCompose = MapGptCompose()
    mapGptCompose.PreviewTheme {
        CustomKeyboardModeCollapsed(
            mapGptCompose = mapGptCompose,
            showCustomChatBubble = true,
        )
    }
}
