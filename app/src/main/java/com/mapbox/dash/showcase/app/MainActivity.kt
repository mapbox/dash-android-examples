package com.mapbox.dash.showcase.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.slider.Slider.OnChangeListener
import com.mapbox.dash.destination.preview.places.DefaultPlacesPreview
import com.mapbox.dash.destination.preview.presentation.DefaultDestinationPreview
import com.mapbox.dash.destination.preview.presentation.DefaultRoutesOverview
import com.mapbox.dash.destination.preview.presentation.compose.DefaultOfflineRouteAlert
import com.mapbox.dash.driver.notification.presentation.DefaultDriverNotificationView
import com.mapbox.dash.driver.presentation.DefaultArrivalFeedbackView
import com.mapbox.dash.driver.presentation.edittrip.DefaultEditTripCard
import com.mapbox.dash.driver.presentation.end.DefaultEndActiveGuidanceView
import com.mapbox.dash.driver.presentation.end.DefaultTripSummary
import com.mapbox.dash.driver.presentation.map.DefaultRangeMapInfoView
import com.mapbox.dash.driver.presentation.markers.DefaultRouteCalloutView
import com.mapbox.dash.driver.presentation.search.DefaultSearchPanelView
import com.mapbox.dash.driver.presentation.searcharea.DefaultSearchAreaButton
import com.mapbox.dash.driver.presentation.streetname.DefaultStreetNameView
import com.mapbox.dash.driver.presentation.waypoint.DefaultContinueNavigationView
import com.mapbox.dash.fullscreen.search.DefaultFullScreenSearch
import com.mapbox.dash.fullscreen.search.favorites.DefaultFavoritesScreen
import com.mapbox.dash.fullscreen.search.favorites.presenation.DefaultEditFavoriteScreen
import com.mapbox.dash.logging.LogsExtra
import com.mapbox.dash.maneuver.presentation.ui.DefaultManeuverView
import com.mapbox.dash.maneuver.presentation.ui.DefaultUpcomingManeuversView
import com.mapbox.dash.route.restore.DefaultResumeGuidanceView
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.dash.sdk.config.api.DEFAULT_3D_STYLE
import com.mapbox.dash.sdk.config.api.EngineType
import com.mapbox.dash.sdk.config.api.ScreenDirectionality
import com.mapbox.dash.sdk.config.api.ThemeFactory
import com.mapbox.dash.sdk.config.api.camera
import com.mapbox.dash.sdk.config.api.destinationPreview
import com.mapbox.dash.sdk.config.api.locationSimulation
import com.mapbox.dash.sdk.config.api.maneuverView
import com.mapbox.dash.sdk.config.api.mapStyle
import com.mapbox.dash.sdk.config.api.network
import com.mapbox.dash.sdk.config.api.routeOptions
import com.mapbox.dash.sdk.config.api.search
import com.mapbox.dash.sdk.config.api.searchPanel
import com.mapbox.dash.sdk.config.api.theme
import com.mapbox.dash.sdk.config.api.ui
import com.mapbox.dash.sdk.config.api.uiSettings
import com.mapbox.dash.sdk.config.api.voices
import com.mapbox.dash.sdk.data.inputs.updateCompassData
import com.mapbox.dash.sdk.map.domain.style.DefaultMapLayerComposer
import com.mapbox.dash.sdk.map.presentation.ui.DefaultRecenterButton
import com.mapbox.dash.sdk.search.api.DashFavoriteType
import com.mapbox.dash.sdk.search.api.DashNavigationSuggestion
import com.mapbox.dash.sdk.search.api.DashSearchRecord
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.dash.sdk.search.api.DashSearchResultType
import com.mapbox.dash.sdk.storage.ExternalProfile
import com.mapbox.dash.showcase.app.databinding.ActivityMainBinding
import com.mapbox.dash.showcase.app.databinding.LayoutCustomizationMenuBinding
import com.mapbox.dash.showcase.app.theme.CustomThemeFactory
import com.mapbox.dash.showcase.app.theme.RedThemeFactory
import com.mapbox.dash.showcase.app.ui.DefaultUiModeMapper
import com.mapbox.dash.showcase.app.ui.ReversedUiModeMapper
import com.mapbox.dash.showcase.app.ui.SampleRouteCalloutView
import com.mapbox.dash.showcase.app.ui.custom.arrival.SampleArrivalView
import com.mapbox.dash.showcase.app.ui.custom.arrival.SampleContinueNavigation
import com.mapbox.dash.showcase.app.ui.custom.edittrip.SampleEditTrip
import com.mapbox.dash.showcase.app.ui.custom.end.SampleEndActiveGuidance
import com.mapbox.dash.showcase.app.ui.custom.maneuver.SampleGuidanceBanner
import com.mapbox.dash.showcase.app.ui.custom.maneuver.SampleUpcomingManeuversBanner
import com.mapbox.dash.showcase.app.ui.custom.notificaiton.SampleDriverNotificationView
import com.mapbox.dash.showcase.app.ui.custom.places.SamplePlacesView
import com.mapbox.dash.showcase.app.ui.custom.searcharea.SampleSearchArea
import com.mapbox.dash.showcase.app.ui.custom.streetname.SampleStreetName
import com.mapbox.dash.showcase.app.ui.custom.tripsummary.SampleTripSummaryView
import com.mapbox.dash.state.defaults.camera.SimpleDefaults
import com.mapbox.dash.theming.Theme
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.mapgpt.mapGpt
import com.mapbox.navigation.mapgpt.mapGptCompose
import com.mapbox.navigation.mapgpt.setDefaultUserInputMiddleware
import com.mapbox.navigation.mapgpt.setDefaultVoicePlayerMiddleware
import com.mapbox.navigation.mapgpt.setUserInputMiddleware
import com.mapbox.navigation.mapgpt.setVoicePlayerMiddleware
import com.mapbox.navigation.mapgpt.stopMapGptConversation
import com.mapbox.navigation.mapgpt.ui.MapGptCarouselCardParams
import com.mapbox.navigation.mapgpt.updateMapGptContextOverrides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import com.mapbox.dash.sdk.config.api.SearchPanelPosition as RawSearchPanelPosition

@Suppress("MagicNumber", "LargeClass")
@OptIn(
    ExperimentalCoroutinesApi::class, ExperimentalPreviewMapboxNavigationAPI::class,
    MapboxExperimental::class, FlowPreview::class,
)
class MainActivity : DrawerActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    private val menuBinding by lazy { LayoutCustomizationMenuBinding.inflate(LayoutInflater.from(this)) }

    private val mapGptVM by viewModels<MapGptViewModel>()
    private val themeVM by viewModels<ThemeViewModel>()
    private val userInputVM by viewModels<UserInputMiddlewareViewModel>()
    private val voicePlayerVM by viewModels<VoicePlayerViewModel>()
    private val layoutVM by viewModels<LayoutViewModel>()
    private val searchVM by viewModels<SearchViewModel>()
    private val mapStyleVM by viewModels<MapStyleViewModel>()
    private val settingsVM by viewModels<SettingsViewModel>()
    private val evViewModel by viewModels<EvViewModel>()

    private val dashNavigationFragmentFlow by lazy {
        layoutVM.headlessMode.map { getDashNavigationFragment() }
    }
    private val weatherController by lazy { WeatherController(dashNavigationFragmentFlow) }

    private val searchItem = buildSearchItem()
    private var searchJob: Job? = null
    private val sensorManager by lazy { requireNotNull(getSystemService<SensorManager>()) }
    private val sampleSensorEventManager by lazy {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (accelerometer == null) {
            Log.d(TAG, "Accelerometer is not available")
            null
        } else if (magnetometer == null) {
            Log.d(TAG, "Magnetometer is not available")
            null
        } else if (!isLocationPermissionGranted()) {
            Log.d(TAG, "LocationPermission is not granted")
            null
        } else {
            SampleSensorEventManager(accelerometer, magnetometer)
        }
    }

    private fun buildSearchItem() = object : DashSearchResult {
        override val address = null
        override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
        override val etaMinutes = null
        override val id = "customHistoryItemId1122334455"
        override val mapboxId = "mapboxId1122334455"
        override val name = "1123 15th Street Northwest"
        override val customName: String? = null
        override val type = DashSearchResultType.ADDRESS
        override val categories = listOf("Some Category")
        override val categoryIds = listOf("some_category")
        override val description = null
        override val distanceMeters = null
        override val metadata = mapOf(
            "extraMetadataKey_1" to "extraValue_1",
            "extraMetadataKey_2" to "extraValue_2",
        )
        override val pinCoordinate = Point.fromLngLat(-77.0342, 38.9044)
    }

    private fun getDashNavigationFragment(): DashNavigationFragment? {
        return supportFragmentManager.findFragmentById(R.id.container) as? DashNavigationFragment
    }

    override fun attachBaseContext(newBase: Context) {
        val originalTabletLayout = newBase.resources.configuration.smallestScreenWidthDp >= 600
        val originalDensityDpi = newBase.resources.configuration.densityDpi
        val tabletLayout = tabletLayout ?: originalTabletLayout.also { tabletLayout = it }
        val densityDpi = densityDpi ?: originalDensityDpi.also { densityDpi = it }
        if (tabletLayout == originalTabletLayout && densityDpi == originalDensityDpi) {
            return super.attachBaseContext(newBase)
        }
        val overrideConfiguration = Configuration(newBase.resources.configuration)
        overrideConfiguration.smallestScreenWidthDp = if (tabletLayout) 600 else 599
        overrideConfiguration.densityDpi = densityDpi
        super.attachBaseContext(newBase.createConfigurationContext(overrideConfiguration))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeHeadlessMode()
        initCustomizationMenu()
        registerEventsObservers()

        bindSwitch(
            menuBinding.toggleCustomPlacesList,
            layoutVM.setCustomPlacesListComposer,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setPlacesPreview { state, modifier ->
                    SamplePlacesView(
                        placesListUiState = state,
                        modifier = modifier,
                    )
                }
            } else {
                fragment.setPlacesPreview { state, modifier ->
                    DefaultPlacesPreview(state = state, modifier = modifier)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomPlacePreview,
            layoutVM.setCustomPlacePreviewComposer,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setDestinationPreview { modifier, state ->
                    SampleDestinationPreview(modifier, state, weatherController)
                }
            } else {
                fragment.setDestinationPreview { modifier, _ ->
                    DefaultDestinationPreview(modifier)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleLocationSimulation,
            settingsVM.locationSimulationEnabled,
        ) { enabled ->
            Dash.applyUpdate {
                locationSimulation {
                    this.locationSimulationEnabled = enabled
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomManeuver,
            layoutVM.setCustomManeuver,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setManeuver { modifier, state ->
                    SampleGuidanceBanner(modifier, state)
                }
            } else {
                fragment.setManeuver { modifier, state ->
                    DefaultManeuverView(modifier, state)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomUpcomingManeuvers,
            layoutVM.setCustomUpcomingManeuvers,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setUpcomingManeuvers { modifier, state ->
                    SampleUpcomingManeuversBanner(modifier, state)
                }
            } else {
                fragment.setUpcomingManeuvers { modifier, state ->
                    DefaultUpcomingManeuversView(modifier, state)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomRoutesOverview,
            layoutVM.setCustomRoutesOverviewComposer,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setRoutesOverview { modifier, routesOverviewState, backCloseButtonState ->
                    SampleRoutesOverview(
                        modifier = modifier,
                        routesOverviewState = routesOverviewState,
                        backCloseButtonState = backCloseButtonState,
                    )
                }
            } else {
                fragment.setRoutesOverview { modifier, routesOverviewState, backCloseButtonState ->
                    DefaultRoutesOverview(modifier, routesOverviewState, backCloseButtonState)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomSearchScreen,
            layoutVM.setCustomSearchScreen,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setFullScreenSearch { modifier, setFullScreenSearch ->
                    SampleFullScreenSearch(modifier = modifier, state = setFullScreenSearch)
                }
                fragment.setFavoritesScreen { modifier, favoritesScreenState ->
                    SampleFavoritesScreen(modifier = modifier, state = favoritesScreenState)
                }
                fragment.setEditFavoriteScreen { modifier, editFavoriteScreenState ->
                    SampleEditFavoriteScreen(modifier = modifier, state = editFavoriteScreenState)
                }
            } else {
                fragment.setFullScreenSearch { modifier, fullScreenSearchState ->
                    DefaultFullScreenSearch(modifier = modifier, state = fullScreenSearchState)
                }
                fragment.setFavoritesScreen { modifier, favoritesScreenState ->
                    DefaultFavoritesScreen(modifier = modifier, state = favoritesScreenState)
                }
                fragment.setEditFavoriteScreen { modifier, editFavoriteScreenState ->
                    DefaultEditFavoriteScreen(modifier = modifier, state = editFavoriteScreenState)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomOfflineAlert,
            layoutVM.setCustomOfflineRouteAlert,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setOfflineRouteAlert { modifier, offlineRouteAlertState ->
                    SampleOfflineRouteAlert(modifier = modifier, state = offlineRouteAlertState)
                }
            } else {
                fragment.setOfflineRouteAlert { modifier, offlineRouteAlertState ->
                    DefaultOfflineRouteAlert(modifier = modifier, state = offlineRouteAlertState)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomResumeGuidanceView,
            layoutVM.setCustomResumeGuidanceView,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setResumeGuidanceView { modifier, resumeGuidanceViewState ->
                    SampleResumeGuidanceView(modifier, resumeGuidanceViewState)
                }
            } else {
                fragment.setResumeGuidanceView { modifier, resumeGuidanceViewState ->
                    DefaultResumeGuidanceView(modifier, resumeGuidanceViewState)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomRangeMapInfoView,
            layoutVM.setCustomRangeMapInfoView,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setRangeMapInfoView { modifier, state ->
                    SampleRangeMapInfoView(modifier, state)
                }
            } else {
                fragment.setRangeMapInfoView { modifier, state ->
                    DefaultRangeMapInfoView(modifier, state)
                }
            }
        }

        bindSwitch(menuBinding.toggleShowConnectivityProblemsInfo, showConnectivityProblemsInfo) { enabled ->
            Dash.applyUpdate {
                Log.d(TAG, "Update showConnectivityProblemsInfo: $enabled")
                ui {
                    this.showOfflineModeInfo = enabled
                }
            }
        }

        layoutVM.navigationSuggestionsEnabled
            .flatMapLatest { enabled ->
                if (enabled) {
                    combine(
                        Dash.controller.observeFavorites(),
                        Dash.controller.observeHistory(),
                    ) { favorites, history -> favorites + history }
                } else {
                    flowOf(emptyList<DashSearchRecord>())
                }
            }
            .map { records -> records.map { record -> DashNavigationSuggestion(record) } }
            .observeWhenStarted(this) { suggestions ->
                Dash.controller.setNavigationSuggestions(suggestions)
            }

        Dash.controller.updateMapGptContextOverrides {
            // 10% Override the context so that MapGPT thinks the electric battery is low.
            vehicleContext.value = { contextDTO ->
                contextDTO.copy(
                    fuel = EngineType.ELECTRIC,
                    batteryLevel = 10,
                )
            }
        }

        if (resources.getBoolean(com.mapbox.dash.theming.R.bool.is_tablet)) {
            Dash.applyUpdate {
                search {
                    keyboardSplitMode = true
                    keyboardWidthDp = 800
                }
            }
        }

        menuBinding.toggleCustomCompassDataInput.setOnCheckedChangeListener { _, isChecked ->
            if (sampleSensorEventManager == null) {
                menuBinding.toggleCustomCompassDataInput.isChecked = false
                Toast.makeText(
                    this@MainActivity,
                    "Failed to create Custom SensorManager",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            layoutVM.setCustomCompassDataInput.value = isChecked && sampleSensorEventManager != null
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                layoutVM.setCustomCompassDataInput
                    .flatMapLatest { enabled ->
                        if (enabled) {
                            sampleSensorEventManager?.compassData ?: emptyFlow()
                        } else {
                            emptyFlow()
                        }
                    }
                    .collect { compassData ->
                        Dash.controller.updateCompassData(compassData)
                    }
            }
        }
    }

    private fun registerSensorListeners() = sampleSensorEventManager?.let { listener ->
        sensorManager.registerListener(
            listener,
            listener.accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
        sensorManager.registerListener(
            listener,
            listener.magnetometer,
            SensorManager.SENSOR_DELAY_NORMAL,
        )
    }

    override fun onResume() {
        super.onResume()
        registerSensorListeners()
    }

    override fun onPause() {
        super.onPause()
        sampleSensorEventManager?.let {
            sensorManager.unregisterListener(it)
        }
    }

    private fun observeHeadlessMode() {
        layoutVM.headlessMode.observeWhenStarted(this) { enabled ->
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            if (enabled) {
                if (currentFragment is HeadlessModeFragment) null else HeadlessModeFragment.newInstance()
            } else {
                if (currentFragment is DashNavigationFragment) null else DashNavigationFragment.newInstance()
            }?.let { newFragment ->
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, newFragment)
                    .commitNow()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Dash.controller.stopMapGptConversation()
    }

    override fun onCreateContentView(): View {
        return binding.root
    }

    override fun onCreateMenuView(): View {
        return menuBinding.root
    }

    // storage for configuration mutations
    private val showDebugLogs = MutableStateFlow(value = true)
    private val connectMapboxStack = MutableStateFlow(value = true)
    private val setMap3dStyle = MutableStateFlow(value = true)
    private val setMapNightStyle = MutableStateFlow(value = true)
    private val setMapSatelliteStyle = MutableStateFlow(value = true)
    private val overrideRouteCallouts = MutableStateFlow(value = false)
    private val setOfflineTts = MutableStateFlow(value = false)
    private val useCustomVoicePlayerMiddleware = MutableStateFlow(value = false)
    private val showConnectivityProblemsInfo = MutableStateFlow(value = false)

    private fun isLocationPermissionGranted(): Boolean {
        val locationPermissions =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                )
            } else {
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            }
        return locationPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun initCustomizationMenu() {
        dashNavigationFragmentFlow.filterNotNull().observeWhenStarted(lifecycleOwner = this) { dashNavigationFragment ->
            dashNavigationFragment.setLeftSidebar(ShowcaseLeftSidebarComposer(layoutVM, dashNavigationFragment))
            dashNavigationFragment.setRightSidebar(ShowcaseRightSidebarComposer(layoutVM, weatherController))
        }
        menuBinding.toggleHeadlessMode.setOnCheckedChangeListener { _, isChecked ->
            layoutVM.headlessMode.value = isChecked
        }
        menuBinding.btnResetNavigationState.bindAction {
            Dash.controller.resetNavigationState(
                // Reset the navigation state to the Free Drive mode
                // and show the route recovery prompt
                shouldShowRouteRecoveryPrompt = true,
            )
        }
        bindSwitch(
            menuBinding.toggleCustomTripSummary,
            layoutVM.setCustomTripSummary,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setTripSummary { modifier, tripSummaryUiState ->
                    SampleTripSummaryView(modifier, tripSummaryUiState, weatherController)
                }
            } else {
                fragment.setTripSummary { modifier, tripSummaryUiState ->
                    DefaultTripSummary(modifier, tripSummaryUiState)
                }
            }
        }
        bindSwitch(
            menuBinding.toggleCustomEndActiveGuidance,
            layoutVM.setCustomEndActiveGuidance,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setEndActiveGuidance { modifier, uiState ->
                    SampleEndActiveGuidance(modifier, uiState)
                }
            } else {
                fragment.setEndActiveGuidance { modifier, uiState ->
                    DefaultEndActiveGuidanceView(modifier, uiState)
                }
            }
        }
        bindSwitch(
            menuBinding.toggleCustomEditTrip,
            layoutVM.setCustomEditTrip,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setEditTrip { _, state ->
                    SampleEditTrip(state = state)
                }
            } else {
                fragment.setEditTrip { modifier, state ->
                    DefaultEditTripCard(state = state, modifier = modifier)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomArrivalFeedback,
            layoutVM.setCustomArrivalFeedback,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setArrivalFeedback { modifier, state ->
                    SampleArrivalView(modifier, state)
                }
            } else {
                fragment.setArrivalFeedback { modifier, state ->
                    DefaultArrivalFeedbackView(state = state, modifier = modifier)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomContinueNavigation,
            layoutVM.setCustomContinueNavigation,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setContinueNavigation { modifier, state ->
                    SampleContinueNavigation(modifier, state)
                }
            } else {
                fragment.setContinueNavigation { modifier, state ->
                    DefaultContinueNavigationView(state = state, modifier = modifier)
                }
            }
        }
        bindSwitch(menuBinding.toggleDebugLogs, showDebugLogs) { isChecked ->
            // mutate config to enable debug logs
            // this uses mutate + apply in a single function
            Dash.applyUpdate {
                logLevel = if (isChecked) LogsExtra.LOG_LEVEL_DEBUG else LogsExtra.LOG_LEVEL_INFO
            }
        }

        bindSwitch(menuBinding.connectMapboxStack, connectMapboxStack) { isConnected ->
            Dash.applyUpdate {
                network { isMapboxStackConnected = isConnected }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomDriverNotification,
            layoutVM.setCustomDriverNotification,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setDriverNotification { modifier, uiState ->
                    SampleDriverNotificationView(modifier, uiState)
                }
            } else {
                fragment.setDriverNotification { modifier, uiState ->
                    DefaultDriverNotificationView(modifier, uiState)
                }
            }
        }

        val themes = CustomDashTheme.entries.map { it.name }
        menuBinding.themeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themes).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        bindSpinner(menuBinding.themeSpinner, themeVM.dashTheme) { name ->
            Dash.applyUpdate {
                theme {
                    themeFactory = CustomDashTheme.valueOf(name).themeFactory
                }
            }
        }
        menuBinding.layersManagerCard.bind(lifecycleOwner = this, dashNavigationFragmentFlow)
        configureMapLayerToggle()
        mapGptCustomizations()
        mapStyleCustomizations()
        settingCustomization()

        bindSwitch(menuBinding.avoidHighways, layoutVM.avoidHighways) { avoidHighways ->
            Dash.applyUpdate {
                routeOptions {
                    this.avoidHighways = avoidHighways
                }
            }
        }

        bindSwitch(menuBinding.avoidTolls, layoutVM.avoidTolls) { avoidTolls ->
            Dash.applyUpdate {
                routeOptions {
                    this.avoidTolls = avoidTolls
                }
            }
        }

        bindSwitch(menuBinding.avoidFerries, layoutVM.avoidFerries) { avoidFerries ->
            Dash.applyUpdate {
                routeOptions {
                    this.avoidFerries = avoidFerries
                }
            }
        }

        bindSwitch(menuBinding.screenDirectionality, layoutVM.leftHandTrafficLayout) { useLeftHandTrafficLayout ->
            Dash.applyUpdate {
                ui {
                    screenDirectionality = when (useLeftHandTrafficLayout) {
                        true -> ScreenDirectionality.RIGHT_TO_LEFT
                        false -> ScreenDirectionality.LEFT_TO_RIGHT
                    }
                }
            }
        }
        bindSwitch(menuBinding.shoveGestureEnabled, layoutVM.shoveGestureEnabled) { shoveGestureEnabled ->
            Dash.applyUpdate {
                camera {
                    isShoveGestureEnabled = shoveGestureEnabled
                }
            }
        }

        menuBinding.btnSetDestination.bindAction {
            val controller = Dash.controller
            val location = controller.observeRawLocation().first()
            val destination = location.getRandomDestinationAround()
            getDashNavigationFragment()?.setDestination(destination)
        }

        menuBinding.btnStartNullNavigation.bindAction {
            Dash.controller.startNavigation(0)
        }

        menuBinding.btnStopNavigation.bindAction {
            val controller = Dash.controller
            controller.stopNavigation()
        }

        menuBinding.btnContinueNavigation.bindAction {
            val controller = Dash.controller
            controller.navigateNextRouteLeg()
        }

        menuBinding.btnShowEvRangeMap.bindAction {
            Dash.controller.showEvRangeMap()
        }

        menuBinding.btnHideEvRangeMap.bindAction {
            Dash.controller.hideEvRangeMap()
        }

        repeatWhenStarted(lifecycleOwner = this) {
            combine(
                Dash.controller.observeEvRangeMapState(),
                dashNavigationFragmentFlow.filterNotNull(),
            ) { state, fragment ->
                fragment.setAdditionalPointsToFrame(state.rangeMapFramePoints)
            }.collect()
        }

        menuBinding.btnOpenSearch.bindAction {
            closeDrawers()
            val query = menuBinding.etSearch.text.toString()
            val newYork = Point.fromLngLat(-73.98200596982161, 40.72726118601179)
            getDashNavigationFragment()?.openSearch(query, newYork)
        }

        menuBinding.btnCloseSearch.bindAction {
            closeDrawers()
            getDashNavigationFragment()?.closeSearch()
        }

        menuBinding.btnSearchOnMap.bindAction {
            closeDrawers()
            val query = menuBinding.etSearch.text.toString()
            getDashNavigationFragment()?.openMapWithSearch(query)
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
            Dash.controller.addFavoriteItem(searchItem, DashFavoriteType.HOME)
        }
        menuBinding.removeFromFavorites.bindAction {
            Dash.controller.removeFavoriteItem(searchItem, DashFavoriteType.HOME)
        }
        menuBinding.btnSearchApi.bindAction {
            // simulate 2 sequentially requests. The first one should be cancelled and
            // Dash.controller.observeSearchRequestStatus() should provide a valid status.
            // delay(200) is needed to give enough time to start a request, otherwise it will be cancelled immediately.
            listOf(
                "aaa",
                menuBinding.etSearchApi.text.toString(),
            ).forEach {
                delay(200)
                performSearch(it)
            }
        }
        menuBinding.setNavigationSuggestions.setOnCheckedChangeListener { _, isChecked ->
            layoutVM.navigationSuggestionsEnabled.value = isChecked
        }
        bindSwitch(menuBinding.setInjectCustomSearch, layoutVM.customSearchResults) { enabled ->
            layoutVM.customSearchResults.value = enabled
        }
        menuBinding.btnCategorySearch.bindAction {
            getDashNavigationFragment()?.openMapWithCategorySearch("cinema", "Cinema")
            closeDrawers()
        }

        menuBinding.freeDrivePitchSlider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                Dash.applyUpdate {
                    camera {
                        freeDriveDefaults = SimpleDefaults(freeDriveDefaults.zoom, value.toDouble())
                    }
                }
            }
        }

        val density = resources.displayMetrics.density
        val paddingSliderChangeListener = OnChangeListener { _, _, _ ->
            getDashNavigationFragment()?.setSafeAreaPaddings(
                (menuBinding.leftPaddingSlider.value * density).roundToInt(),
                (menuBinding.topPaddingSlider.value * density).roundToInt(),
                (menuBinding.rightPaddingSlider.value * density).roundToInt(),
                (menuBinding.bottomPaddingSlider.value * density).roundToInt(),
            )
        }
        menuBinding.leftPaddingSlider.addOnChangeListener(paddingSliderChangeListener)
        menuBinding.topPaddingSlider.addOnChangeListener(paddingSliderChangeListener)
        menuBinding.rightPaddingSlider.addOnChangeListener(paddingSliderChangeListener)
        menuBinding.bottomPaddingSlider.addOnChangeListener(paddingSliderChangeListener)

        bindSwitch(menuBinding.toggleSidebarControls, layoutVM.overrideSidebarControls)

        menuBinding.spinnerSearchPanelPosition.adapter =
            ArrayAdapter(this, R.layout.item_spinner, SearchPanelPosition.entries.map { it.name })
        bindSpinner(
            menuBinding.spinnerSearchPanelPosition,
            layoutVM.searchPanelPosition,
        ) { name ->
            val position = SearchPanelPosition.valueOf(name)
            Dash.applyUpdate {
                ui {
                    searchPanel {
                        this.position = when (position) {
                            SearchPanelPosition.TopLeft -> RawSearchPanelPosition.TOP_LEFT
                            SearchPanelPosition.BottomLeft -> RawSearchPanelPosition.BOTTOM_LEFT
                        }
                    }
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomSearchPanel,
            layoutVM.overrideSearchPanelButtons,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setSearchPanel { modifier, searchPanelState ->
                    SampleSearchPanel(modifier = modifier, state = searchPanelState, fragment = fragment)
                }
            } else {
                fragment.setSearchPanel { modifier, searchPanelState ->
                    DefaultSearchPanelView(modifier = modifier, state = searchPanelState)
                }
            }
        }
        bindSwitch(
            menuBinding.overrideSearchAreaButton,
            layoutVM.overrideSearchThisArea,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setSearchArea { uiState ->
                    SampleSearchArea(uiState)
                }
            } else {
                fragment.setSearchArea { uiState ->
                    DefaultSearchAreaButton(uiState)
                }
            }
        }
        bindSwitch(
            menuBinding.toggleCustomRecenterPill,
            layoutVM.overrideRecenterPill,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setRecenterCamera { modifier, recenterCameraState ->
                    Image(
                        modifier = modifier
                            .width(dimensionResource(id = com.mapbox.dash.theming.R.dimen.map_round_button_width))
                            .height(dimensionResource(id = com.mapbox.dash.theming.R.dimen.map_round_button_height))
                            .background(Color.White)
                            .clickable(onClick = recenterCameraState.onRecenterClick)
                            .padding(dimensionResource(id = com.mapbox.dash.theming.R.dimen.round_button_padding)),
                        painter = painterResource(R.drawable.baseline_my_location_24),
                        contentDescription = "Recenter button",
                    )
                }
            } else {
                fragment.setRecenterCamera { modifier, recenterCameraState ->
                    DefaultRecenterButton(modifier, recenterCameraState)
                }
            }
        }

        bindSwitch(
            menuBinding.toggleCustomStreetNameView,
            layoutVM.setCustomStreetName,
            dashNavigationFragmentFlow,
        ) { fragment, enabled ->
            if (enabled) {
                fragment.setStreetNameLabel { modifier, uiState -> SampleStreetName(modifier, uiState) }
            } else {
                fragment.setStreetNameLabel { modifier, uiState -> DefaultStreetNameView(modifier, uiState) }
            }
        }

        bindSwitch(menuBinding.toggleCustomMarkerFactory, layoutVM.setCustomMarkerFactory) { enabled ->
            Dash.applyUpdate {
                mapStyle {
                    markerFactory = if (enabled) {
                        SampleMarkerFactory(this@MainActivity)
                    } else {
                        null
                    }
                }
            }
        }
        bindSwitch(menuBinding.toggleSimpleCardHeader, layoutVM.simpleCardHeader) { enabled ->
            Dash.applyUpdate {
                destinationPreview {
                    titleSingleLine = enabled
                }
                ui {
                    showCloseButtonInCards = !enabled
                }
            }
        }
        bindSwitch(menuBinding.toggleUpcomingLaneGuidance, layoutVM.upcomingLaneGuidance) { enabled ->
            Dash.applyUpdate {
                ui {
                    maneuverView {
                        showUpcomingLaneGuidance = enabled
                    }
                }
            }
        }

        menuBinding.customSearchEnginePolicySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            searchVM.allPolicies,
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        bindSpinner(
            menuBinding.customSearchEnginePolicySpinner,
            searchVM.selectedPolicyName,
        ) { name ->
            searchVM.setPolicy(name)
        }

        menuBinding.toggleTabletLayout.isChecked = tabletLayout == true
        menuBinding.toggleTabletLayout.setOnCheckedChangeListener { _, isChecked ->
            if (tabletLayout != isChecked) {
                tabletLayout = isChecked
                recreate()
            }
        }

        menuBinding.currentDensity.text = getString(R.string.current_density, densityDpi)
        menuBinding.overrideDensity.setOnClickListener {
            val editText = EditText(this)
            editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
            editText.setText(densityDpi?.toString().orEmpty())
            AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("OK") { _, _ ->
                    val newDensityDpi = editText.text.toString().toIntOrNull()
                    if (densityDpi != newDensityDpi) {
                        densityDpi = newDensityDpi
                        recreate()
                    }
                }
                .setNeutralButton("RESET") { _, _ ->
                    densityDpi = null
                    recreate()
                }
                .setNegativeButton("CANCEL", null)
                .show()
        }

        Dash.controller.observeExternalProfile().observeWhenStarted(lifecycleOwner = this) { profile ->
            menuBinding.currentProfile.text = getString(R.string.current_profile, profile?.id)
        }
        menuBinding.switchProfile.setOnClickListener {
            showProfileAlert { profileId ->
                Dash.controller.setExternalProfile(profileId?.let { ExternalProfile(it) })
            }
        }
        menuBinding.removeProfile.setOnClickListener {
            showProfileAlert { profileId ->
                lifecycleScope.launch {
                    Dash.controller.removeExternalProfile(profileId)
                }
            }
        }

        menuBinding.btnShowRoutes.setOnClickListener {
            getDashNavigationFragment()?.showRoutesOverview()
        }

        val engineTypes = settingsVM.engineTypes
        val modeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, engineTypes)
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.engineType.adapter = modeAdapter
        bindSpinner(
            spinner = menuBinding.engineType,
            state = settingsVM.engineType,
            onSelected = { type ->
                Dash.applyUpdate {
                    engineType = type
                }
            },
        )
    }

    private fun showProfileAlert(onDone: (String?) -> Unit) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                onDone(editText.text.toString())
            }
            .setNeutralButton("DEFAULT") { _, _ ->
                onDone(null)
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            Dash.controller.search(query)
        }
    }

    private fun configureMapLayerToggle() {
        bindSwitch(
            menuBinding.enableWeatherAlongRoute,
            layoutVM.enableWeatherAlongRoute,
            dashNavigationFragmentFlow,
        ) { fragment, enableWeatherAlongRoute ->
            if (enableWeatherAlongRoute) {
                layoutVM.enableMapLayer.value = false
                layoutVM.enableEvChargePoint.value = false
                fragment.setMapLayer {
                    topSlot {
                        WeatherAlongRouteBlock(weatherController.weatherWarningsAlongRoute) { message ->
                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                fragment.setMapLayer(DefaultMapLayerComposer)
            }
        }

        bindSwitch(
            menuBinding.enableEvChargePoint,
            layoutVM.enableEvChargePoint,
            dashNavigationFragmentFlow,
        ) { fragment, enableEvChargePoint ->
            if (enableEvChargePoint) {
                layoutVM.enableMapLayer.value = false
                layoutVM.enableWeatherAlongRoute.value = false
                fragment.setMapLayer {
                    topSlot {
                        EvChargePointBlock(evViewModel.chargePoints) { message ->
                            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                fragment.setMapLayer(DefaultMapLayerComposer)
            }
        }

        bindSwitch(
            menuBinding.enableMapLayer,
            layoutVM.enableMapLayer,
            dashNavigationFragmentFlow,
        ) { fragment, enableMapLayer ->
            if (enableMapLayer) {
                layoutVM.enableWeatherAlongRoute.value = false
                layoutVM.enableEvChargePoint.value = false
                fragment.setMapLayer {
                    middleSlot {
                        CustomLayerBlock()
                    }

                    topSlot {
                        WeatherLayer()
                    }
                }
            } else {
                fragment.setMapLayer(DefaultMapLayerComposer)
            }
        }
    }

    private fun mapGptCustomizations() {
        val avatarNames = mapGptVM.availableAvatarNames
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, avatarNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.avatarsSpinner.adapter = adapter
        bindSpinner(
            spinner = menuBinding.avatarsSpinner,
            state = mapGptVM.mapGptAvatarName,
            onSelected = { avatarName ->
                mapGptCompose.config.value = mapGptCompose.config.value.build {
                    avatar = mapGptVM.sampleAvatars[avatarName]
                }
            },
        )
        bindSwitch(menuBinding.enableMapGpt, mapGptVM.mapGptEnabled) { isChecked ->
            Dash.applyUpdate {
                mapGpt {
                    isEnabled = isChecked
                }
            }
        }
        bindMapGptComposeConfigSwitch(
            switch = menuBinding.showAvatar,
            config = mapGptCompose.config,
            getValue = { showAvatar },
            setValue = { showAvatar = it },
        )
        bindMapGptComposeConfigSwitch(
            switch = menuBinding.showKeyboardMode,
            config = mapGptCompose.config,
            getValue = { showKeyboardMode },
            setValue = { showKeyboardMode = it },
        )
        bindMapGptComposeConfigSwitch(
            switch = menuBinding.showCarousel,
            config = mapGptCompose.config,
            getValue = { showCarousel },
            setValue = { showCarousel = it },
        )
        bindMapGptComposeConfigSwitch(
            switch = menuBinding.showChatBubble,
            config = mapGptCompose.config,
            getValue = { showChatBubble },
            setValue = { showChatBubble = it },
        )
        bindMapGptCarouselCardParams(
            switch = menuBinding.customMapGptCarouselCardParams,
            config = mapGptCompose.mapGptCarouselCardParams,
            setValue = {
                if (it) {
                    MapGptCarouselCardParams(
                        backgroundColor = Color.Magenta,
                        backgroundShape = RoundedCornerShape(0.dp),
                        paddingStart = 16.dp,
                        paddingVertical = 8.dp,
                        paddingEnd = 0.dp,
                        maxWidth = Dp.Unspecified,
                    )
                } else {
                    null
                }
            },
        )
        bindSwitch(menuBinding.customChatBubble, mapGptVM.mapGptCustomChatBubble)
    }

    private fun mapStyleCustomizations() {
        bindSwitch(menuBinding.set3dMapStyle, setMap3dStyle) { enabled ->
            Dash.applyUpdate {
                mapStyle {
                    map3dStyleUri = if (enabled) DEFAULT_3D_STYLE else ""
                }
            }
        }
        bindSwitch(menuBinding.setNightMapStyle, setMapNightStyle) { enabled ->
            Dash.applyUpdate {
                mapStyle {
                    nightStyleUri = if (enabled) ShowcaseApp.NIGHT_MAP_STYLE else ""
                }
            }
        }
        bindSwitch(menuBinding.setSatelliteMapStyle, setMapSatelliteStyle) { enabled ->
            Dash.applyUpdate {
                mapStyle {
                    satelliteStyleUri = if (enabled) ShowcaseApp.SATELLITE_MAP_STYLE else ""
                }
            }
        }
        bindSwitch(menuBinding.setReverseUiMode, layoutVM.reverseUiMode) { enabled ->
            Dash.applyUpdate {
                ui {
                    uiModeMapper = if (enabled) ReversedUiModeMapper else DefaultUiModeMapper
                }
            }
        }
        menuBinding.spinnerUserInputOwnerMiddleware.adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            userInputVM.availableModels(),
        ).apply {
            setDropDownViewResource(R.layout.item_spinner)
        }
        bindSpinner(
            menuBinding.spinnerUserInputOwnerMiddleware,
            userInputVM.selectedModelName,
        ) { model ->
            val userInputMiddleware = userInputVM.getUserInputMiddleware(model)
            if (userInputMiddleware == null) {
                Dash.controller.setDefaultUserInputMiddleware()
            } else {
                Dash.controller.setUserInputMiddleware(userInputMiddleware)
            }
        }
        bindSwitch(
            menuBinding.overrideRouteCallouts,
            overrideRouteCallouts,
            dashNavigationFragmentFlow,
        ) { fragment, overrideRouteCallout ->
            fragment.setRouteCallout { state ->
                if (overrideRouteCallout) {
                    SampleRouteCalloutView(state, fragment)
                } else {
                    DefaultRouteCalloutView(state)
                }
            }
        }
        bindSwitch(menuBinding.setTtsOfflineMode, setOfflineTts) { setOfflineTts ->
            Dash.applyUpdate {
                voices {
                    preferLocalTts = setOfflineTts
                }
            }
        }
        bindSwitch(
            menuBinding.useCustomVoicePlayerMiddleware,
            useCustomVoicePlayerMiddleware,
        ) { useCustomVoicePlayerMiddleware ->
            if (useCustomVoicePlayerMiddleware) {
                Dash.controller.setVoicePlayerMiddleware(LocalVoicePlayerMiddleware())
            } else {
                Dash.controller.setDefaultVoicePlayerMiddleware()
            }
        }
        menuBinding.spinnerRemoteTtsProvider.adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            availableTtsProviders,
        ).apply {
            setDropDownViewResource(R.layout.item_spinner)
        }
        bindSpinner(
            menuBinding.spinnerRemoteTtsProvider,
            voicePlayerVM.remoteTtsProviderKey,
        ) { provider ->
            Dash.applyUpdate {
                voices { remoteTtsProvider = provider }
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
        ) { selection ->
            val puck = CustomLocationPuck.valueOf(selection).getLocationPuck(this)
            Dash.applyUpdate {
                theme {
                    locationPuck = puck
                }
            }
        }

        val mapStyleModeNames = mapStyleVM.mapStyleModeNames
        val modeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mapStyleModeNames)
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.mapStyleMode.adapter = modeAdapter
        bindSpinner(
            spinner = menuBinding.mapStyleMode,
            state = mapStyleVM.mapStyleMode,
            onSelected = { mode ->
                Dash.applyUpdate {
                    ui {
                        mapStyleMode = mode
                    }
                }
            },
        )

        val mapStyleThemeNames = mapStyleVM.mapStyleThemeNames
        val themeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mapStyleThemeNames)
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.mapStyleTheme.adapter = themeAdapter
        bindSpinner(
            spinner = menuBinding.mapStyleTheme,
            state = mapStyleVM.mapStyleTheme,
            onSelected = { theme ->
                Dash.applyUpdate {
                    ui {
                        mapStyleTheme = theme
                    }
                }
            },
        )

        val uiModeNames = mapStyleVM.uiModeNames
        val uiModeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, uiModeNames)
        uiModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.uiMode.adapter = uiModeAdapter
        bindSpinner(
            spinner = menuBinding.uiMode,
            state = mapStyleVM.uiMode,
            onSelected = { mode ->
                Dash.applyUpdate {
                    ui {
                        uiModeSettings = mode
                    }
                }
            },
        )
    }

    private fun settingCustomization() {
        bindSwitch(menuBinding.showRouteOptions, layoutVM.showRouteOptionsInSettings) { showRouteOptions ->
            Dash.applyUpdate {
                uiSettings {
                    this.showRouteOptions = showRouteOptions
                }
            }
        }

        bindSwitch(
            menuBinding.showSpeedLimitsOptions,
            layoutVM.showSpeedLimitsOptionsInSettings,
        ) { showSpeedLimitsOptions ->
            Dash.applyUpdate {
                uiSettings {
                    this.showSpeedLimitsOptions = showSpeedLimitsOptions
                }
            }
        }
    }

    private fun registerEventsObservers() {
        dashNavigationFragmentFlow
            .flatMapLatest { it?.observeMapEvents() ?: emptyFlow() }
            .observeWhenStarted(lifecycleOwner = this) { log(">> DashMapEvent | event = $it") }
        Dash.controller.observeGenericEvents().observeWhenStarted(this) { event ->
            log(">> DashGenericEvent | event = $event")
        }
        Dash.controller.observeRouteProgress().observeWhenStarted(this) { progress ->
            progress.apply {
                log(">> RouteProgress | distance: $distanceRemaining duration: $durationRemaining")
            }
        }
        Dash.controller.observeRawLocation().observeWhenStarted(this) {
            log(">> Location | location = $it")
        }
        Dash.controller.observeLocationMatcherResult().observeWhenStarted(this) {
            log(">> Location | locationMatcherResult = $it")
        }
        Dash.controller.observeRoutes().observeWhenStarted(this) { event ->
            val routes = event.routes
            val activeRoute = routes.firstOrNull()?.id
            log(">> ActiveRoutes | routes = ${routes.map { it.id }}; activeRoute = $activeRoute")
        }
        dashNavigationFragmentFlow
            .flatMapLatest { it?.observeRoutes() ?: emptyFlow() }
            .observeWhenStarted(this) { event ->
                val routes = event.routes
                val displayedRoute = routes.firstOrNull()?.id
                log(">> DisplayedRoutes | routes = ${routes.map { it.id }}; displayedRoute = $displayedRoute")
            }
        Dash.controller.observeNavigationState().observeWhenStarted(this) {
            log(">> NavigationState | $it")
        }
        dashNavigationFragmentFlow
            .flatMapLatest { it?.observeUiState() ?: emptyFlow() }
            .observeWhenStarted(this) {
                log(">> UiState | $it")
            }
        Dash.controller.observeNextManeuver().observeWhenStarted(this) {
            log(">> Maneuver | $it")
        }
        Dash.controller.observeNavigationEvents().observeWhenStarted(this) {
            log(">> NavigationEvent | $it")
        }
        Dash.controller.observeRoadInfoEvents().observeWhenStarted(this) {
            log(">> DashRoadInfoEvent | $it")
        }
        Dash.controller.observeHistory().observeWhenStarted(this) { history ->
            val list = history.map {
                "(${it.name} | ${it.address?.toString()} | ${it.metadata})"
            }
            log(">> History. Items count = ${history.size} | $list")
        }
        Dash.controller.observeFavorites().observeWhenStarted(this) { favorites ->
            val list = favorites.joinToString("\n") {
                val address = it.address?.toString()
                "(${it.name} | $address | ${it.favoriteType} | ${it.metadata})"
            }
            log(">> Favorites. Items count = ${favorites.size} | $list")
        }
        Dash.controller.observeSearchResults().observeWhenStarted(this) { results ->
            log(">> Search results. size = ${results.size}")
//           log(">> Search results. Update ETA and distance")
//            Dash.controller.addEtaAndDistanceToSearchResults(results).forEachIndexed { index, result ->
//                log(">> Search results [$index]: eta = ${result.etaMinutes}, distance = ${result.distanceMeters}")
//            }
        }
        Dash.controller.observeSearchSuggestions().observeWhenStarted(this) { suggestions ->
            val list = suggestions.joinToString("\n") { "(${it.name} | ${it.address?.toString()})" }
            log(">> Search suggestions. Items count = ${suggestions.size} | $list")
        }
        dashNavigationFragmentFlow
            .flatMapLatest { it?.observeCameraState() ?: emptyFlow() }
            .debounce(2.seconds)
            .observeWhenStarted(this) {
                log(">> DashCameraState | state= $it")
            }
        Dash.controller.observeSearchRequestState().observeWhenStarted(this) {
            log(">> DashSearchRequestStatus | $it")
        }
        dashNavigationFragmentFlow
            .flatMapLatest { it?.observeMapMarkers() ?: emptyFlow() }
            .observeWhenStarted(this) { markers ->
                log(">> Map markers. Items count = ${markers.size}")
            }
        Dash.controller.observeDebugBundleReady().observeWhenStarted(this) { file ->
            log(">> Debug bundle ${file.absolutePath} is ready")
        }
        Dash.controller.observeRouteWaypoints().observeWhenStarted(this) { waypoints ->
            log(">> observeRouteWaypoints. size = ${waypoints.size}")
            waypoints.forEachIndexed { index, item ->
                log(">> observeRouteWaypoints. waypoint[$index]: category = ${item.categories}")
            }
        }

        lifecycleScope.launch {
            log(">> getOfflineRegionMetadata = ${Dash.controller.getOfflineRegionMetadata()}")
        }
    }

    private fun log(message: String) = Log.d(TAG, message)

    private companion object {

        const val TAG = "MainActivity"

        private var tabletLayout: Boolean? = null
        private var densityDpi: Int? = null
    }
}

internal enum class SearchPanelPosition {
    BottomLeft, TopLeft,
}

internal enum class CustomDashTheme(
    val themeFactory: ThemeFactory,
) {

    DEFAULT(ThemeFactory(::Theme)),
    CUSTOM(CustomThemeFactory),
    RED(RedThemeFactory),
}
