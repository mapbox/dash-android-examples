package com.mapbox.dash.showcase.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.compose.content
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.NavigationState
import com.mapbox.dash.showcase.app.menu.MenuButton
import com.mapbox.navigation.mapgpt.attachMapGptPermissionLauncher
import com.mapbox.navigation.mapgpt.detachMapGptPermissionLauncher
import com.mapbox.navigation.mapgpt.mapGptCompose
import kotlinx.coroutines.flow.first

/**
 * A simple [Fragment] subclass.
 * Use the [HeadlessModeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HeadlessModeFragment : Fragment() {

    private val mapGptViewModel by activityViewModels<MapGptViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Dash.controller.startTripSession().onFailure {
            Toast.makeText(requireContext(), "Failed to start trip session", Toast.LENGTH_SHORT).show()
        }
        Dash.controller.attachMapGptPermissionLauncher(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = content {
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

    override fun onDestroy() {
        super.onDestroy()
        Dash.controller.detachMapGptPermissionLauncher(this)
        Dash.controller.stopTripSession()
    }

    companion object {

        @JvmStatic
        fun newInstance() = HeadlessModeFragment()
    }
}
