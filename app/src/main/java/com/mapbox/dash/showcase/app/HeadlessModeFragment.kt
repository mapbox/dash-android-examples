package com.mapbox.dash.showcase.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.NavigationState
import com.mapbox.dash.showcase.app.databinding.FragmentHeadlessModeBinding
import com.mapbox.navigation.mapgpt.MapGptDetachableUI
import com.mapbox.navigation.mapgpt.attachMapGptPermissionLauncher
import com.mapbox.navigation.mapgpt.detachMapGptPermissionLauncher
import com.mapbox.navigation.mapgpt.mapGptCompose
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * A simple [Fragment] subclass.
 * Use the [HeadlessModeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HeadlessModeFragment : Fragment() {

    private val mapGptViewModel: MapGptViewModel by activityViewModels()

    private lateinit var binding: FragmentHeadlessModeBinding

    private var mapGptUI: MapGptDetachableUI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Dash.controller.attachMapGptPermissionLauncher(this)

        Dash.controller.observeNavigationState().observeWhenStarted(this) {
            binding.tvNavigationState.text = it::class.java.simpleName
        }

        Dash.controller.observeRouteProgress().map { it.distanceRemaining.toInt() }
            .observeWhenStarted(this) {
                binding.tvDistanceRemaining.text = "$it m."
            }
        Dash.controller.observeNavigationState()
            .distinctUntilChangedBy { it::class.java.simpleName }
            .observeWhenStarted(this) { state ->
                val button = binding.btnChangeNavigationState
                val controller = Dash.controller
                when (state) {
                    is NavigationState.ActiveGuidance -> {
                        button.isVisible = true
                        button.text = "Stop navigation"
                        button.bindAction {
                            controller.stopNavigation()
                        }
                    }

                    is NavigationState.FreeDrive -> {
                        button.isVisible = true
                        button.text = "Start navigation"
                        button.bindAction {
                            val location = controller.observeRawLocation().first()
                            val destination = location.getRandomDestinationAround()
                            val routes = controller.fetchRoutes(destination)
                            controller.startNavigation(routes.getOrThrow().first())
                        }
                    }

                    else -> {
                        button.isVisible = false
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment
        binding = FragmentHeadlessModeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapGptUI = mapGptCompose.attachContent(binding.mapGptComposeView) {
            val showCustomChatBubble = mapGptViewModel.mapGptCustomChatBubble.collectAsState().value
            CustomMapGptUI(
                mapGptCompose = mapGptCompose,
                showCustomChatBubble = showCustomChatBubble,
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapGptUI?.detach()
        mapGptUI = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Dash.controller.detachMapGptPermissionLauncher(this)
    }

    companion object {

        @JvmStatic
        fun newInstance() = HeadlessModeFragment()
    }
}
