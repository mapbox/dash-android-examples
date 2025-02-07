package com.mapbox.dash.example

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mapbox.dash.example.databinding.FragmentHeadlessModeBinding
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.NavigationState
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * A simple [Fragment] subclass.
 * Use the [HeadlessModeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HeadlessModeFragment : Fragment() {

    private lateinit var binding: FragmentHeadlessModeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Dash.controller.observeNavigationState().observeWhenStarted(this) {
            binding.tvNavigationState.text = it.javaClass.simpleName
        }

        Dash.controller.observeRouteProgress().map { it.distanceRemaining.toInt() }
            .observeWhenStarted(this) {
                binding.tvDistanceRemaining.text = "$it m."
            }
        Dash.controller.observeNavigationState()
            .distinctUntilChangedBy { it.javaClass.simpleName }
            .observeWhenStarted(this) {
                val button = binding.btnChangeNavigationState
                val controller = Dash.controller
                when (it) {
                    is NavigationState.ActiveGuidance -> {
                        button.isVisible = true
                        button.text = "Stop navigation"
                        button.bindAction {
                            controller.stopNavigation()
                        }
                    }

                    is NavigationState.FreeDrive -> {
                        button.isVisible = true
                        button.text = "Set destination"
                        button.bindAction {
                            val location = controller.observeRawLocation().first()
                            val destination = location.getRandomDestinationAround()
                            controller.setDestination(destination)
                        }
                    }

                    is NavigationState.TripPlanning -> {
                        button.isVisible = true
                        button.text = "Start navigation"
                        button.bindAction {
                            controller.startNavigation(controller.observeRoutes().first().routes.first())
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
        // Inflate the layout for this fragment
        binding = FragmentHeadlessModeBinding.inflate(inflater)
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = HeadlessModeFragment()
    }
}
