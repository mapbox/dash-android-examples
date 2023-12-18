package com.mapbox.dash.example

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.mapbox.dash.example.databinding.ActivityMainBinding
import com.mapbox.dash.example.databinding.LayoutCustomizationMenuBinding
import com.mapbox.dash.logging.LogsExtra
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.dash.sdk.base.flow.observeWhenStarted
import com.mapbox.dash.sdk.config.api.DashMapStyleConfig
import com.mapbox.dash.sdk.config.api.NullableConfigUpdate
import com.mapbox.dash.sdk.coordination.PointDestination
import com.mapbox.dash.sdk.search.DashFavoriteType
import com.mapbox.dash.sdk.search.DashSearchResult
import com.mapbox.dash.sdk.search.DashSearchResultType
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : DrawerActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var menuBinding: LayoutCustomizationMenuBinding

    private val mapGptVM: MapGptViewModel by viewModels()
    private val themeVM: ThemeViewModel by viewModels()

    private val headlessMode = MutableStateFlow(false)

    private val searchItem = buildSearchItem()
    private val favoriteItem = buildSearchItem(DashFavoriteType.HOME)

    private fun buildSearchItem(@DashFavoriteType.Type favoriteType: String? = null) = object : DashSearchResult {
        override val address = null
        override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
        override val etaMinutes = null
        override val id = "customHistoryItemId1122334455"
        override val name = "1123 15th Street Northwest"
        override val type = DashSearchResultType.ADDRESS
        override val categories = listOf("some category")
        override val description = null
        override val distanceMeters = null
        override val favoriteType = favoriteType
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        // Start trip session to enable Navigation SDK
        Dash.controller.startTripSession().onFailure {
            Toast.makeText(this, "Failed to start trip session", Toast.LENGTH_SHORT).show()
        }

        if (savedInstanceState == null) {
            // After initializing in your `MainApplication` class,
            // now you can transition to a new Dash Fragment instance
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DashNavigationFragment.newInstance())
                .commitNow()
        }

        initCustomizationMenu()
        registerEventsObservers()
        headlessMode.observeWhenStarted(this) { enabled ->
            val fragment = if (enabled) {
                HeadlessModeFragment.newInstance()
            } else {
                DashNavigationFragment.newInstance()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commitNow()
        }
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
    private var showDebugLogs = MutableStateFlow(true)
    private var setNightMapStyle =
        MutableStateFlow(Dash.config.mapStyleConfig.nightStyleUri.isNotBlank())
    private var setSatelliteMapStyle =
        MutableStateFlow(Dash.config.mapStyleConfig.satelliteStyleUri.isNotBlank())
    private var setMap3dStyle = MutableStateFlow(Dash.config.mapStyleConfig.map3dStyleUri.isNotBlank())
    private var setOfflineTts = MutableStateFlow(Dash.config.preferLocalTts)
    private var showRouteOptionsInSettings = MutableStateFlow(Dash.config.uiSettingsConfig.showRouteOptions)
    private var showSpeedLimitsOptionsInSettings = MutableStateFlow(Dash.config.uiSettingsConfig.showSpeedLimitsOptions)

    private fun initCustomizationMenu() {
        headlessModeCustomization()
        logsCustomization()
        themeCustomization()
        mapGptCustomization()
        mapStyleCustomization()
        settingCustomization()
        offlineTtsCustomization()
        dashCoordination()
    }

    private fun headlessModeCustomization() {
        bindSwitch(
            switch = menuBinding.toggleHeadlessMode,
            state = headlessMode,
        ) { enabled ->
            headlessMode.value = enabled
        }
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
        val themes = CustomDashTheme.names()
        menuBinding.themeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        menuBinding.themeSpinner.setSelection(themes.indexOf(themeVM.dashTheme.value))
        bindSpinner(
            menuBinding.themeSpinner,
            themeVM.dashTheme,
        ) {
            it?.also { name ->
                Dash.applyUpdate {
                    themeConfig {
                        val t = CustomDashTheme.valueOf(name)
                        dayStyleRes = t.dayResId
                        nightStyleRes = t.nightResId
                    }
                }
            }
        }
    }

    private fun mapGptCustomization() {
        val avatarNames = mapGptVM.availableAvatarNames
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, avatarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.avatarsSpinner.adapter = adapter
        menuBinding.avatarsSpinner.setSelection(avatarNames.indexOf(mapGptVM.mapGptAvatarName.value))
        bindSpinner(
            spinner = menuBinding.avatarsSpinner,
            liveData = mapGptVM.mapGptAvatarName,
            onSelected = { avatarName ->
                mapGptVM.sampleAvatars[avatarName]?.let { avatarUpdate ->
                    Dash.applyUpdate {
                        mapGptConfig {
                            avatar = NullableConfigUpdate(avatarUpdate)
                        }
                    }
                }
            },
        )
        bindSwitch(
            switch = menuBinding.enableMapGpt,
            liveData = mapGptVM.isMapGptEnabled,
        ) { isChecked ->
            Dash.applyUpdate {
                mapGptConfig {
                    isEnabled = isChecked
                }
            }
        }
        bindSwitch(
            switch = menuBinding.enableKeyboardMode,
            liveData = mapGptVM.isMapGptKeyboardModeEnabled,
        ) { isChecked ->
            Dash.applyUpdate {
                mapGptConfig {
                    isKeyboardModeEnabled = isChecked
                }
            }
        }
    }

    private fun mapStyleCustomization() {
        bindSwitch(
            switch = menuBinding.set3dMapStyle,
            state = setMap3dStyle,
        ) { enabled ->
            Dash.applyUpdate {
                mapStyleConfig {
                    map3dStyleUri = if (enabled) DashMapStyleConfig.create().map3dStyleUri else ""
                }
            }
        }
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
        menuBinding.spinnerNavPuck.adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            CustomLocationPuck.names(),
        ).apply {
            setDropDownViewResource(R.layout.item_spinner)
        }
        bindSpinner(
            menuBinding.spinnerNavPuck,
            themeVM.locationPuck,
        ) {
            if (it != null) {
                val puck = CustomLocationPuck.valueOf(it).getLocationPuck(this)
                Dash.applyUpdate {
                    themeConfig {
                        locationPuck = puck
                    }
                }
            }
        }
    }

    private fun settingCustomization() {
        bindSwitch(
            switch = menuBinding.showRouteOptions,
            state = showRouteOptionsInSettings,
        ) { showRouteOptions ->
            Dash.applyUpdate {
                uiSettingsConfig {
                    this.showRouteOptions = showRouteOptions
                }
            }
        }

        bindSwitch(
            switch = menuBinding.showSpeedLimitsOptions,
            state = showSpeedLimitsOptionsInSettings,
        ) { showSpeedLimitsOptions ->
            Dash.applyUpdate {
                uiSettingsConfig {
                    this.showSpeedLimitsOptions = showSpeedLimitsOptions
                }
            }
        }


    }

    private fun offlineTtsCustomization() {
        bindSwitch(
            switch = menuBinding.setTtsOfflineMode,
            state = setOfflineTts,
        ) { setOfflineTts ->
            Dash.applyUpdate {
                preferLocalTts = setOfflineTts
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

        menuBinding.cleanHistory.bindAction {
            Dash.controller.cleanHistory()
        }
        menuBinding.addToHistory.bindAction {
            Dash.controller.addHistoryItem(searchItem)
        }
        menuBinding.removeFromHistory.bindAction {
            Dash.controller.removeHistoryItem(searchItem)
        }
        menuBinding.addToFavorites.bindAction {
            Dash.controller.addFavoriteItem(favoriteItem)
        }
        menuBinding.removeFromFavorites.bindAction {
            Dash.controller.removeFavoriteItem(favoriteItem)
        }
        menuBinding.btnSearchApi.bindAction {
            Dash.controller.search(menuBinding.etSearchApi.text.toString())
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
        Dash.controller.observeNavigationState().observeWhenStarted(this) {
            println(">> NavigationState | $it")
        }
        Dash.controller.observeNextManeuver().observeWhenStarted(this) {
            println(">> Maneuver | $it")
        }
        Dash.controller.observeNavigationEvents().observeWhenStarted(this) {
            println(">> NavigationEvent | $it")
        }
        Dash.controller.observeHistory().observeWhenStarted(this) { history ->
            println(">> History. Items count = ${history.size}")
            history.forEach {
                println(">> History item | $it")
            }
        }
        Dash.controller.observeFavorites().observeWhenStarted(this) { favorites ->
            println(">> Favorites. Items count = ${favorites.size}")
            favorites.forEach {
                println(">> Favorite item | $it")
            }
        }
        Dash.controller.observeSearchResults().observeWhenStarted(this) { results ->
            println(">> Search results. Items count = ${results.size}")
            results.forEach {
                println(">> Search result | $it")
            }
        }
        Dash.controller.observeSearchSuggestions().observeWhenStarted(this) { suggestions ->
            println(">> Search suggestions. Items count = ${suggestions.size}")
            suggestions.forEach {
                println(">> Search suggestion | $it")
            }
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

    internal enum class CustomDashTheme(
        val dayResId: Int,
        val nightResId: Int,
    ) {
        DEFAULT(com.mapbox.dash.themes.R.style.DashTheme_Day, com.mapbox.dash.themes.R.style.DashTheme_Night),
        CUSTOM(R.style.MyDashTheme_Day, R.style.MyDashTheme_Night),
        RED(R.style.MyDashThemeRed_Day, R.style.MyDashThemeRed_Night),
        ;

        companion object {
            fun names() = values().map { it.name }
        }
    }

    private companion object {

        const val TAG = "MainActivity"
    }
}
