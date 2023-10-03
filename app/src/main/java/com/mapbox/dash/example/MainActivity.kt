package com.mapbox.dash.example

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.mapbox.dash.app.Dash
import com.mapbox.dash.app.DashNavigationFragment
import com.mapbox.dash.app.config.api.DashMapStyleConfig
import com.mapbox.dash.app.coordination.PointDestination
import com.mapbox.dash.example.databinding.ActivityMainBinding
import com.mapbox.dash.example.databinding.LayoutCustomizationMenuBinding
import com.mapbox.dash.logging.LogsExtra
import com.mapbox.nav.gm.base.flow.observeWhenStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : DrawerActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuBinding: LayoutCustomizationMenuBinding

    private val viewModel: MapGptAvatarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        if (savedInstanceState == null) {
            // After initializing in your `MainApplication` class,
            // now you can transition to a new Dash Fragment instance
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DashNavigationFragment.newInstance())
                .commitNow()
        }

        initCustomizationMenu()
        registerEventsObservers()
    }

    override fun onCreateContentView(): View {
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        return binding.root
    }

    override fun onCreateMenuView(): View {
        menuBinding = LayoutCustomizationMenuBinding.inflate(LayoutInflater.from(this))
        return menuBinding.root
    }

    // storage for configuration mutations
    private var showDebugLogs = MutableStateFlow(false)
    private var useCustomTheme = MutableStateFlow(false)
    private var setNightMapStyle =
        MutableStateFlow(Dash.config.mapStyleConfig.nightStyleUri.isNotBlank())
    private var setSatelliteMapStyle =
        MutableStateFlow(Dash.config.mapStyleConfig.satelliteStyleUri.isNotBlank())

    private fun initCustomizationMenu() {
        logsCustomization()
        themeCustomization()
        mapGptCustomization()
        mapStyleCustomization()
        dashCoordination()
    }

    private fun logsCustomization() {
        bindSwitch(
            switch = menuBinding.toggleDebugLogs,
            state = showDebugLogs,
        ) { isChecked ->
            // mutate config to toggle between debug and info log levels
            Dash.applyUpdate {
                logLevel = if (isChecked) LogsExtra.LOG_LEVEL_DEBUG else LogsExtra.LOG_LEVEL_INFO
            }
        }
    }

    private fun themeCustomization() {
        bindSwitch(
            switch = menuBinding.toggleTheme,
            state = useCustomTheme,
        ) { isChecked ->
            // mutate config to change the theme
            Dash.applyUpdate {
                themeConfig {
                    dayStyleRes = if (isChecked) R.style.DayThemeRed else R.style.DayTheme
                }
            }
        }
    }

    private fun mapGptCustomization() {
        val avatarNames = viewModel.availableAvatarNames
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, avatarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.avatarsSpinner.adapter = adapter
        menuBinding.avatarsSpinner.setSelection(avatarNames.indexOf(viewModel.mapGptAvatarName.value))
        bindSpinner(
            spinner = menuBinding.avatarsSpinner,
            liveData = viewModel.mapGptAvatarName,
            onSelected = { avatarName ->
                viewModel.sampleAvatars[avatarName].let { avatarUpdate ->
                    // mutate config to change the MapGPT avatar
                    // you can create your own avatar using Lottie animations by instantiating `LottieMapGptAvatar` class
                    Dash.applyUpdate {
                        mapGptConfig {
                            avatar(avatarUpdate)
                        }
                    }
                }
            },
        )
    }

    private fun mapStyleCustomization() {
        bindSwitch(
            switch = menuBinding.setNightMapStyle,
            state = setNightMapStyle,
        ) { enabled ->
            // mutate the config to enable/disable Night Map Style
            Dash.applyUpdate {
                mapStyleConfig {
                    nightStyleUri = if (enabled) DashMapStyleConfig.create().nightStyleUri else ""
                }
            }
        }
        bindSwitch(
            switch = menuBinding.setSatelliteMapStyle,
            state = setSatelliteMapStyle,
        ) { enabled ->
            // mutate the config to enable/disable Satellite Map Style
            Dash.applyUpdate {
                mapStyleConfig {
                    satelliteStyleUri =
                        if (enabled) DashMapStyleConfig.create().satelliteStyleUri else ""
                }
            }
        }
    }

    private fun dashCoordination() {
        // force a new destination and show a route preview
        bindButton(button = menuBinding.btnSetDestination) {
            val controller = Dash.controller
            val location = controller.observeRawLocation().first()
            val destination = location.getRandomDestinationAround()
            controller.setDestination(destination)
        }

        // stop an active navigation session, if there's one
        bindButton(button = menuBinding.btnStopNavigation) {
            val controller = Dash.controller
            controller.stopNavigation()
        }
    }

    private fun registerEventsObservers() {
        Dash.controller.observeMapEvents().observeWhenStarted(this) { event ->
            Log.d(TAG, ">> DashMapEvent | event = $event")
        }
        Dash.controller.observeMapGptEvents().observeWhenStarted(this) { event ->
            Log.d(TAG, ">> DashMapGptEvent | event = $event")
        }
        Dash.controller.observeRouteProgress().observeWhenStarted(this) { progress ->
            progress.apply {
                Log.d(
                    TAG,
                    ">> RouteProgress | distance: $distanceRemaining duration: $durationRemaining"
                )
            }
        }
        Dash.controller.observeRawLocation().observeWhenStarted(this) {
            Log.d(TAG, ">> Location | location = $it")
        }
        Dash.controller.observeLocationMatcherResult().observeWhenStarted(this) {
            Log.d(TAG, ">> Location | locationMatcherResult = $it")
        }
        Dash.controller.observeRoutes().observeWhenStarted(this) { event ->
            val routes = event.routes
            val activeRoute = routes.firstOrNull()?.id
            Log.d(TAG, ">> Routes | routes = ${routes.map { it.id }}; activeRoute = $activeRoute")
        }
    }

    private fun Location.getRandomDestinationAround(): PointDestination {
        val radiusInDegrees: Double = 2000.0 / 111000.0
        val random = Random()
        val u: Double = random.nextDouble()
        val v: Double = random.nextDouble()
        val w = radiusInDegrees * sqrt(u)
        val t = 2 * Math.PI * v
        val x = w * cos(t)
        val y = w * sin(t)

        // Adjust the x-coordinate for the shrinking of the east-west distances
        val newX = x / cos(Math.toRadians(latitude))
        return PointDestination(longitude = longitude + newX, latitude = latitude + y)
    }

    private companion object {

        const val TAG = "MainActivity"
    }
}