package com.mapbox.dash.showcase.app

import android.widget.Toast
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.NavigationState
import com.mapbox.dash.showcase.app.menu.MenuButton
import com.mapbox.navigation.mapgpt.attachMapGptPermissionLauncher
import com.mapbox.navigation.mapgpt.detachMapGptPermissionLauncher
import com.mapbox.navigation.mapgpt.mapGptCompose
import kotlinx.coroutines.flow.first
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

@Composable
fun HeadlessModeScreen(mapGptViewModel: MapGptViewModel) {
    val uuid = rememberSaveable { UUID.randomUUID().toString() }
    val activityResultRegistry = requireNotNull(LocalActivityResultRegistryOwner.current).activityResultRegistry
    val context = LocalContext.current
    val activityResultCaller = remember { LocalActivityResultCaller(uuid, activityResultRegistry) }
    DisposableEffect(Unit) {
        Dash.controller.startTripSession().onFailure {
            Toast.makeText(context, "Failed to start trip session", Toast.LENGTH_SHORT).show()
        }
        Dash.controller.attachMapGptPermissionLauncher(activityResultCaller)
        onDispose {
            Dash.controller.detachMapGptPermissionLauncher(activityResultCaller)
            Dash.controller.stopTripSession()
        }
    }
    Column(
        modifier = Modifier
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorResource(android.R.color.holo_blue_dark))
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
                )
                .padding(horizontal = 15.dp, vertical = 10.dp),
            text = "DASH Headless Mode. No UI.",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        val state = produceState<NavigationState?>(null) {
            Dash.controller.observeNavigationState().collect { value = it }
        }.value
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .padding(start = 16.dp, top = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(all = 16.dp),
                text = "Navigation state:",
                fontSize = 18.sp,
            )
            Text(
                modifier = Modifier.padding(all = 16.dp),
                text = state?.let { it::class.java.simpleName.uppercase() } ?: "-",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(all = 16.dp),
                text = "Distance remaining",
                fontSize = 18.sp,
            )
            Text(
                modifier = Modifier.padding(all = 16.dp),
                text = produceState("-") {
                    Dash.controller.observeRouteProgress()
                        .collect { value = "${it.distanceRemaining.toInt()} m.".uppercase() }
                }.value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        when (state) {
            is NavigationState.ActiveGuidance -> MenuButton(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = 24.dp)
                    .width(IntrinsicSize.Min),
                text = "STOP\u00A0NAVIGATION",
                onClick = { Dash.controller.stopNavigation() },
            )
            is NavigationState.FreeDrive -> MenuButton(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = 24.dp)
                    .width(IntrinsicSize.Min),
                text = "START\u00A0NAVIGATION",
                onClick = {
                    val location = Dash.controller.observeRawLocation().first()
                    val destination = location.getRandomDestinationAround()
                    val routes = Dash.controller.fetchRoutes(destination)
                    Dash.controller.startNavigation(routes.getOrThrow().first())
                },
            )
            else -> Unit
        }
        mapGptCompose.Content {
            CustomMapGptUI(
                mapGptCompose = mapGptCompose,
                showCustomChatBubble = mapGptViewModel.mapGptCustomChatBubble.value,
            )
        }
    }
}

private class LocalActivityResultCaller(
    private val uuid: String,
    private val activityResultRegistry: ActivityResultRegistry,
) : ActivityResultCaller {

    private val requestCode = AtomicInteger()

    override fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>,
    ): ActivityResultLauncher<I> {
        val key = "HeadlessModeScreen-$uuid-${requestCode.getAndIncrement()}"
        return activityResultRegistry.register(key, contract, callback)
    }

    override fun <I, O> registerForActivityResult(
        contract: ActivityResultContract<I, O>,
        registry: ActivityResultRegistry,
        callback: ActivityResultCallback<O>,
    ): ActivityResultLauncher<I> {
        val key = "HeadlessModeScreen-$uuid-${requestCode.getAndIncrement()}"
        return registry.register(key, contract, callback)
    }
}
