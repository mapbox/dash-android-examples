package com.mapbox.dash.example

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
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
import androidx.appcompat.widget.AppCompatSpinner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.slider.Slider
import com.mapbox.dash.destination.preview.places.DefaultPlacesPreview
import com.mapbox.dash.destination.preview.presentation.DefaultDestinationPreview
import com.mapbox.dash.destination.preview.presentation.DefaultRoutesOverview
import com.mapbox.dash.destination.preview.presentation.compose.DefaultOfflineRouteAlert
import com.mapbox.dash.driver.notification.presentation.DefaultDriverNotificationView
import com.mapbox.dash.driver.presentation.DefaultArrivalFeedbackView
import com.mapbox.dash.driver.presentation.edittrip.DefaultEditTripCard
import com.mapbox.dash.driver.presentation.end.DefaultTripSummary
import com.mapbox.dash.driver.presentation.map.DefaultRangeMapInfoView
import com.mapbox.dash.driver.presentation.search.DefaultSearchPanelView
import com.mapbox.dash.driver.presentation.waypoint.DefaultContinueNavigationView
import com.mapbox.dash.example.databinding.ActivityMainBinding
import com.mapbox.dash.example.databinding.LayoutCustomizationMenuBinding
import com.mapbox.dash.example.relaxedmode.RelaxedModeActivity
import com.mapbox.dash.example.ui.SampleArrivalFeedback
import com.mapbox.dash.example.ui.SampleContinueNavigation
import com.mapbox.dash.example.ui.SampleDestinationPreview
import com.mapbox.dash.example.ui.SampleDriverNotificationView
import com.mapbox.dash.example.ui.SampleEditFavoriteScreen
import com.mapbox.dash.example.ui.SampleEditTrip
import com.mapbox.dash.example.ui.SampleFavoritesScreen
import com.mapbox.dash.example.ui.SampleFullScreenSearch
import com.mapbox.dash.example.ui.SampleGuidanceBanner
import com.mapbox.dash.example.ui.SampleOfflineRouteAlert
import com.mapbox.dash.example.ui.SamplePlacesView
import com.mapbox.dash.example.ui.SampleRangeMapInfoView
import com.mapbox.dash.example.ui.SampleResumeGuidanceView
import com.mapbox.dash.example.ui.SampleRoutesOverview
import com.mapbox.dash.example.ui.SampleSearchPanel
import com.mapbox.dash.example.ui.SampleTripSummaryView
import com.mapbox.dash.example.ui.SampleUpcomingManeuversBanner
import com.mapbox.dash.fullscreen.search.DefaultFullScreenSearch
import com.mapbox.dash.fullscreen.search.favorites.DefaultFavoritesScreen
import com.mapbox.dash.fullscreen.search.favorites.presenation.DefaultEditFavoriteScreen
import com.mapbox.dash.logging.LogsExtra
import com.mapbox.dash.maneuver.presentation.ui.DefaultManeuverView
import com.mapbox.dash.maneuver.presentation.ui.DefaultUpcomingManeuversView
import com.mapbox.dash.route.restore.DefaultResumeGuidanceView
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.dash.sdk.config.api.DashSidebarControl
import com.mapbox.dash.sdk.config.dsl.DEFAULT_3D_STYLE
import com.mapbox.dash.sdk.config.dsl.DashSidebarUpdate
import com.mapbox.dash.sdk.config.dsl.DashUiUpdate
import com.mapbox.dash.sdk.config.dsl.camera
import com.mapbox.dash.sdk.config.dsl.destinationPreview
import com.mapbox.dash.sdk.config.dsl.etaPanel
import com.mapbox.dash.sdk.config.dsl.leftSidebar
import com.mapbox.dash.sdk.config.dsl.maneuverView
import com.mapbox.dash.sdk.config.dsl.mapStyle
import com.mapbox.dash.sdk.config.dsl.rightSidebar
import com.mapbox.dash.sdk.config.dsl.searchPanel
import com.mapbox.dash.sdk.config.dsl.theme
import com.mapbox.dash.sdk.config.dsl.ui
import com.mapbox.dash.sdk.config.dsl.uiSettings
import com.mapbox.dash.sdk.config.dsl.voices
import com.mapbox.dash.sdk.map.domain.style.DefaultMapLayerComposer
import com.mapbox.dash.sdk.search.api.DashFavoriteType
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.dash.sdk.search.api.DashSearchResultType
import com.mapbox.dash.sdk.storage.ExternalProfile
import com.mapbox.dash.state.defaults.camera.SimpleDefaults
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.weather.model.WeatherAlert
import com.mapbox.navigation.weather.model.WeatherCondition
import com.mapbox.navigation.weather.model.WeatherSystemOfMeasurement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import com.mapbox.dash.sdk.config.api.SearchPanelPosition as RawSearchPanelPosition

class MainActivity : DrawerActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    private val menuBinding by lazy { LayoutCustomizationMenuBinding.inflate(LayoutInflater.from(this)) }

    private val themeVM: ThemeViewModel by viewModels()
    private val mapStyleVM: MapStyleViewModel by viewModels()
    private val weatherVM: WeatherViewModel by viewModels()
    private val headlessMode = MutableStateFlow(false)

    private val searchItem = buildSearchItem()
    private val favoriteItem = buildSearchItem()

    private var sampleSensorEventManager: SampleSensorEventManager? = null

    private fun buildSearchItem() =
        object : DashSearchResult {
            override val address = null
            override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
            override val customName: String? = null
            override val etaMinutes = null
            override val id = "customHistoryItemId1122334455"
            override val mapboxId: String? = null
            override val metadata: Map<String, String>? = emptyMap()
            override val name = "1123 15th Street Northwest"
            override val pinCoordinate: Point = coordinate
            override val type = DashSearchResultType.ADDRESS
            override val categories = listOf("some category")
            override val description = null
            override val distanceMeters = null
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
        Log.d(TAG, "onCreate")

        // Start trip session to enable Navigation SDK
        Dash.controller.startTripSession().onFailure {
            Toast.makeText(this, "Failed to start trip session", Toast.LENGTH_SHORT).show()
        }

        if (savedInstanceState == null) {
            // After initializing in your `MainApplication` class,
            // now you can transition to a new Dash Fragment instance
            supportFragmentManager.commitNow { replace(R.id.container, DashNavigationFragment.newInstance()) }
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
        sampleSensorEventManager = SampleSensorEventManager(this)
    }

    override fun onResume() {
        super.onResume()
        sampleSensorEventManager?.onResume()
    }

    override fun onPause() {
        super.onPause()
        sampleSensorEventManager?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        sampleSensorEventManager = null
    }

    override fun onCreateContentView(): View {
        return binding.root
    }

    override fun onCreateMenuView(): View {
        return menuBinding.root
    }

    // storage for configuration mutations
    private val showDebugLogs = MutableStateFlow(value = true)
    private val setMap3dStyle = MutableStateFlow(value = true)
    private val addMapLayer = MutableStateFlow(value = false)
    private val showWeatherWarningAlongRoute = MutableStateFlow(value = false)
    private val setOfflineTts = MutableStateFlow(value = false)
    private val setCustomCompassDataInputs = MutableStateFlow(value = false)
    private val showRouteOptionsInSettings = MutableStateFlow(value = false)
    private val showSpeedLimitsOptionsInSettings = MutableStateFlow(value = false)
    private val leftSidebarMode = MutableStateFlow(SidebarMode.Transparent.name)
    private val rightSidebarMode = MutableStateFlow(SidebarMode.Transparent.name)
    private val overrideSidebarControls = MutableStateFlow(value = false)
    private val searchPanelPosition = MutableStateFlow(SearchPanelPosition.BottomLeft.name)
    private val setCustomArrivalFeedbackComposer = MutableStateFlow(value = false)
    private val setCustomContinueNavigationComposer = MutableStateFlow(value = false)
    private val setCustomGuidanceComposer = MutableStateFlow(value = false)
    private val setCustomUpcomingManeuversComposer = MutableStateFlow(value = false)
    private val setCustomSearchPanel = MutableStateFlow(value = false)
    private val setCustomMarkerFactory = MutableStateFlow(value = false)
    private val setCustomPlacesListComposer = MutableStateFlow(value = false)
    private val setCustomDestinationPreviewComposer = MutableStateFlow(value = false)
    private val setCustomRoutesOverviewComposer = MutableStateFlow(value = false)
    private val setCustomTripSummaryComposer = MutableStateFlow(value = false)
    private val setCustomEditTripComposer = MutableStateFlow(value = false)
    private val setCustomSearchScreen = MutableStateFlow(value = false)
    private val setCustomOfflineRouteAlert = MutableStateFlow(value = false)
    private val setCustomResumeGuidanceView = MutableStateFlow(value = false)
    private val showTripProgress = MutableStateFlow(value = true)
    private val simpleCardHeader = MutableStateFlow(value = false)
    private val upcomingLaneGuidance = MutableStateFlow(value = false)
    private val setCustomVoicePlayer = MutableStateFlow(value = false)
    private val setCustomDriverNotification = MutableStateFlow(value = false)
    private val setCustomRangeMapInfoView = MutableStateFlow(value = false)

    private fun initCustomizationMenu() {
        headlessModeCustomization()
        logsCustomization()
        themeCustomization()
        mapStyleCustomization()
        settingCustomization()
        offlineTtsCustomization()
        dashCoordination()
        etaPanelCustomization()
    }

    private fun headlessModeCustomization() {
        menuBinding.toggleHeadlessMode.setOnCheckedChangeListener { _, isChecked ->
            headlessMode.value = isChecked
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
        menuBinding.themeSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, themes).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        bindSpinner(
            menuBinding.themeSpinner,
            themeVM.dashTheme,
        ) { name ->
            Dash.applyUpdate {
                theme {
                    val t = CustomDashTheme.valueOf(name)
                    dayStyleRes = t.dayResId
                    nightStyleRes = t.nightResId
                }
            }
        }
    }

    @OptIn(MapboxExperimental::class)
    private fun mapStyleCustomization() {
        bindSwitch(
            switch = menuBinding.set3dMapStyle,
            state = setMap3dStyle,
        ) { enabled ->
            Dash.applyUpdate {
                mapStyle {
                    map3dStyleUri = if (enabled) DEFAULT_3D_STYLE else ""
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
            val puck = CustomLocationPuck.valueOf(it).getLocationPuck(this)
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
        bindSwitch(
            switch = menuBinding.showWeatherWarningAlongRoute,
            state = showWeatherWarningAlongRoute,
        ) { enableWeatherAlongRoute ->
            getDashNavigationFragment()?.let { fragment ->
                if (enableWeatherAlongRoute) {
                    addMapLayer.value = false
                    fragment.setMapLayer {
                        topSlot {
                            WeatherAlongRouteBlock(weatherVM.weatherWarningsAlongRoute) { message ->
                                Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    fragment.setMapLayer(DefaultMapLayerComposer)
                }
            }
        }
        bindSwitch(
            switch = menuBinding.addCustomMapLayer,
            state = addMapLayer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
                if (enabled) {
                    showWeatherWarningAlongRoute.value = false
                    fragment.setMapLayer {

                        middleSlot {
                            FireHydrantsLayer()
                        }

                        topSlot {
                            WeatherLayer()
                        }
                    }
                } else {
                    fragment.setMapLayer { DefaultMapLayerComposer() }
                }
            }
        }

        menuBinding.layersManagerCard.bind(this)
    }

    private fun settingCustomization() {
        bindSwitch(
            switch = menuBinding.showRouteOptions,
            state = showRouteOptionsInSettings,
        ) { showRouteOptions ->
            Dash.applyUpdate {
                uiSettings {
                    this.showRouteOptions = showRouteOptions
                }
            }
        }

        bindSwitch(
            switch = menuBinding.showSpeedLimitsOptions,
            state = showSpeedLimitsOptionsInSettings,
        ) { showSpeedLimitsOptions ->
            Dash.applyUpdate {
                uiSettings {
                    this.showSpeedLimitsOptions = showSpeedLimitsOptions
                }
            }
        }
    }

    private fun etaPanelCustomization() {
        bindSwitch(
            switch = menuBinding.toggleTripProgress,
            state = showTripProgress,
        ) { isChecked ->
            Dash.applyUpdate {
                etaPanel {
                    showTripProgress = isChecked
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
                voices {
                    preferLocalTts = setOfflineTts
                }
            }
        }
    }

    @OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
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

        menuBinding.btnShowEvRangeMap.bindAction {
            Dash.controller.showEvRangeMap()
        }

        menuBinding.btnHideEvRangeMap.bindAction {
            Dash.controller.hideEvRangeMap()
        }

        menuBinding.customCompassDataInputs.setOnCheckedChangeListener { _, isChecked ->
            setCustomCompassDataInputs.value = isChecked
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                setCustomCompassDataInputs.collectLatest { enabled ->
                    if (!enabled) return@collectLatest
                    sampleSensorEventManager?.compassData?.collect { compassData ->
                        Log.d(TAG, "Updating compass data: $compassData")
                        Dash.controller.updateCompassData(compassData)
                    }
                }
            }
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
            Dash.controller.addFavoriteItem(favoriteItem, DashFavoriteType.REGULAR)
        }
        menuBinding.removeFromFavorites.bindAction {
            Dash.controller.removeFavoriteItem(favoriteItem)
        }
        menuBinding.btnSearchApi.bindAction {
            Dash.controller.search(menuBinding.etSearchApi.text.toString())
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
        val paddingSliderChangeListener = Slider.OnChangeListener { _, _, _ ->
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

        bindSidebarSpinner(menuBinding.spinnerLeftSidebar, leftSidebarMode, DashUiUpdate::leftSidebar)
        bindSidebarSpinner(menuBinding.spinnerRightSidebar, rightSidebarMode, DashUiUpdate::rightSidebar)

        bindSwitch(
            switch = menuBinding.toggleSidebarControls,
            state = overrideSidebarControls,
        ) { enabled ->
            Dash.applyUpdate {
                ui {
                    rightSidebar {
                        controls = if (enabled) {
                            listOf(
                                DashSidebarControl.Custom(
                                    content = { modifier ->
                                        WeatherAlertWidget(
                                            modifier = modifier,
                                            weatherAlertAtMapCenter = weatherVM.weatherAlertsAtMapCenter,
                                        )
                                    },
                                ),
                                DashSidebarControl.Button(
                                    iconId = R.drawable.baseline_remove_red_eye_24,
                                    onClick = {
                                        val intent = Intent(
                                            this@MainActivity,
                                            RelaxedModeActivity::class.java,
                                        )
                                        this@MainActivity.startActivity(intent)
                                    },
                                ),
                                DashSidebarControl.Speed,
                                DashSidebarControl.Space(),
                                DashSidebarControl.Button(
                                    iconId = R.drawable.ic_waving_hand,
                                    onClick = {
                                        Toast.makeText(this@MainActivity, "Hey, Dash!", Toast.LENGTH_SHORT).show()
                                    },
                                ),
                                DashSidebarControl.Routes,
                                DashSidebarControl.Debug,
                                DashSidebarControl.Custom {
                                    CurrentWeatherWidget(it, weatherVM.weatherConditionAtMapCenter)
                                },
                            )
                        } else {
                            DashSidebarControl.defaultRightSidebarControls
                        }
                    }
                }
            }
        }

        menuBinding.spinnerSearchPanelPosition.adapter =
            ArrayAdapter(this, R.layout.item_spinner, SearchPanelPosition.values().map { it.name })
        bindSpinner(
            menuBinding.spinnerSearchPanelPosition,
            searchPanelPosition,
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
            switch = menuBinding.toggleCustomArrivalFeedbackComposer,
            state = setCustomArrivalFeedbackComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
                if (enabled) {
                    fragment.setArrivalFeedback { modifier, state ->
                        SampleArrivalFeedback(modifier, state)
                    }
                } else {
                    fragment.setArrivalFeedback { modifier, state ->
                        DefaultArrivalFeedbackView(modifier, state)
                    }
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomContinueComposer,
            state = setCustomContinueNavigationComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
                if (enabled) {
                    fragment.setContinueNavigation { modifier, state ->
                        SampleContinueNavigation(modifier, state)
                    }
                } else {
                    fragment.setContinueNavigation { modifier, state ->
                        DefaultContinueNavigationView(modifier, state)
                    }
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomGuidanceComposer,
            state = setCustomGuidanceComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
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
        }

        bindSwitch(
            switch = menuBinding.toggleCustomUpcomingManeuvers,
            state = setCustomUpcomingManeuversComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
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
        }

        bindSwitch(
            switch = menuBinding.toggleCustomSearchPanel,
            state = setCustomSearchPanel,
        ) { enabled ->
            val fragment = getDashNavigationFragment() ?: return@bindSwitch
            if (enabled) {
                fragment.setSearchPanel { modifier, searchPanelState ->
                    SampleSearchPanel(modifier = modifier, state = searchPanelState)
                }
            } else {
                fragment.setSearchPanel { modifier, searchPanelState ->
                    DefaultSearchPanelView(modifier = modifier, state = searchPanelState)
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomMarkerFactory,
            state = setCustomMarkerFactory,
        ) { enabled ->
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

        bindSwitch(
            switch = menuBinding.toggleCustomPlacesList,
            state = setCustomPlacesListComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
                if (enabled) {
                    fragment.setPlacesPreview { state, modifier ->
                        val lazyListState: LazyListState = rememberLazyListState()
                        SamplePlacesView(
                            lazyListState = lazyListState,
                            placesListUiState = state,
                            modifier = modifier,
                        )
                    }
                } else {
                    fragment.setPlacesPreview { state, modifier ->
                        DefaultPlacesPreview(
                            state = state,
                            modifier = modifier
                        )
                    }
                }
            }
        }


        bindSwitch(
            switch = menuBinding.toggleCustomDestinationPreview,
            state = setCustomDestinationPreviewComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
                if (enabled) {
                    fragment.setDestinationPreview { modifier, state ->
                        SampleDestinationPreview(modifier, state, weatherVM)
                    }
                } else {
                    fragment.setDestinationPreview { modifier, _ ->
                        DefaultDestinationPreview(modifier)
                    }
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomRoutesOverview,
            state = setCustomRoutesOverviewComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
                if (enabled) {
                    fragment.setRoutesOverview { modifier, routesOverviewState, backCloseButtonState ->
                        SampleRoutesOverview(
                            modifier = modifier,
                            routesOverviewState = routesOverviewState,
                            backCloseButtonState = backCloseButtonState,
                        )
                    }
                } else {
                    fragment.setRoutesOverview { modifier, routesOverviewState, _ ->
                        DefaultRoutesOverview(modifier = modifier, state = routesOverviewState)
                    }
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomTripSummary,
            state = setCustomTripSummaryComposer,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
                if (enabled) {
                    fragment.setTripSummary { modifier, tripSummaryUiState ->
                        SampleTripSummaryView(modifier, tripSummaryUiState, weatherVM)
                    }
                } else {
                    fragment.setTripSummary { modifier, tripSummaryUiState ->
                        DefaultTripSummary(modifier, tripSummaryUiState)
                    }
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomEditTrip,
            state = setCustomEditTripComposer,
        ) { enabled ->
            getDashNavigationFragment()?.setEditTrip { modifier, state ->
                if (enabled) {
                    SampleEditTrip(state)
                } else {
                    DefaultEditTripCard(state = state, modifier = modifier)
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomSearchScreen,
            state = setCustomSearchScreen,
        ) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
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
        }

        bindSwitch(menuBinding.toggleCustomOfflineAlert, setCustomOfflineRouteAlert) { enabled ->
            val fragment = getDashNavigationFragment() ?: return@bindSwitch
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

        bindSwitch(menuBinding.toggleCustomResumeGuidanceView, setCustomResumeGuidanceView) { enabled ->
            val fragment = getDashNavigationFragment() ?: return@bindSwitch
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

        bindSwitch(menuBinding.toggleCustomVoicePlayer, setCustomVoicePlayer) { enabled ->
            if (enabled) {
                Dash.controller.setVoicePlayerMiddleware(LocalVoicePlayerMiddleware())
            } else {
                Dash.controller.setDefaultVoicePlayerMiddleware()
            }
        }

        bindSwitch(
            switch = menuBinding.toggleSimpleCardHeader,
            state = simpleCardHeader,
        ) { enabled ->
            Dash.applyUpdate {
                destinationPreview {
                    titleSingleLine = enabled
                }
                ui {
                    showCloseButtonInCards = !enabled
                }
            }
        }

        bindSwitch(menuBinding.toggleUpcomingLaneGuidance, upcomingLaneGuidance) { enabled ->
            Dash.applyUpdate {
                ui {
                    maneuverView {
                        showUpcomingLaneGuidance = enabled
                    }
                }
            }
        }

        bindSwitch(menuBinding.toggleCustomDriverNotification, setCustomDriverNotification) { enabled ->
            getDashNavigationFragment()?.let { fragment ->
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
        }

        bindSwitch(menuBinding.toggleCustomRangeMapInfoView, setCustomRangeMapInfoView) { enabled ->
            val fragment = getDashNavigationFragment() ?: return@bindSwitch
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
            fun List<DashSearchResult>.logEtaAndDistance() {
                forEachIndexed { index, result ->
                    println(
                        ">> Search result [$index]: " +
                            "name = ${result.name}, " +
                            "eta = ${result.etaMinutes}, " +
                            "distance = ${result.distanceMeters}"
                    )
                }
            }
            results.logEtaAndDistance()
            println(">> Search results. Update ETA and distance")
            Dash.controller.addEtaAndDistanceToSearchResults(results).logEtaAndDistance()
        }
        Dash.controller.observeSearchSuggestions().observeWhenStarted(this) { suggestions ->
            println(">> Search suggestions. Items count = ${suggestions.size}")
            suggestions.forEach {
                println(">> Search suggestion | $it")
            }
        }
        Dash.controller.observeDebugBundleReady().observeWhenStarted(this) { file ->
            println(">> Debug bundle ${file.absolutePath} is ready")
        }
        Dash.controller.observeRouteWaypoints().observeWhenStarted(this) { waypoints ->
            println(">> observeRouteWaypoints. size = ${waypoints.size}")
            waypoints.forEachIndexed { index, item ->
                println(">> observeRouteWaypoints. waypoint[$index]: categories = ${item.categories}")
            }
        }
    }

    internal enum class CustomDashTheme(
        val dayResId: Int,
        val nightResId: Int,
    ) {

        DEFAULT(
            com.mapbox.dash.theming.R.style.DashTheme_Day,
            com.mapbox.dash.theming.R.style.DashTheme_Night
        ),
        CUSTOM(R.style.MyDashTheme_Day, R.style.MyDashTheme_Night),
        RED(R.style.MyDashThemeRed_Day, R.style.MyDashThemeRed_Night),
        ;

        companion object {

            fun names() = values().map { it.name }
        }
    }

    private fun bindSidebarSpinner(
        spinner: AppCompatSpinner,
        sidebarMode: MutableStateFlow<String>,
        updateSidebarConfig: DashUiUpdate.(DashSidebarUpdate.() -> Unit) -> Unit,
    ) {
        spinner.adapter = ArrayAdapter(this, R.layout.item_spinner, SidebarMode.values().map { it.name })
        bindSpinner(
            spinner,
            sidebarMode,
        ) { name ->
            val mode = SidebarMode.valueOf(name)
            Dash.applyUpdate {
                ui {
                    updateSidebarConfig {
                        visible = mode != SidebarMode.Hidden
                        background = if (mode == SidebarMode.Transparent) {
                            Color.TRANSPARENT
                        } else {
                            ColorUtils.setAlphaComponent(Color.GRAY, 128)
                        }
                    }
                }
            }
        }
    }

    private enum class SidebarMode {
        Transparent, Background, Hidden,
    }

    private enum class SearchPanelPosition {
        BottomLeft, TopLeft,
    }

    private companion object {

        private const val TAG = "MainActivity"

        private var tabletLayout: Boolean? = null
        private var densityDpi: Int? = null
    }
}

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
@Composable
private fun WeatherAlertWidget(modifier: Modifier, weatherAlertAtMapCenter: Flow<List<WeatherAlert>>) {
    val alerts = weatherAlertAtMapCenter.collectAsState(null).value?.joinToString("\n") { it.title }
        ?.takeIf { it.isNotBlank() } ?: "No weather alerts in the center of the map"

    Text(
        modifier = modifier
            .shadow(4.dp, shape = CircleShape)
            .border(
                width = 2.dp,
                color = androidx.compose.ui.graphics.Color.Black,
                shape = CircleShape,
            )
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(all = 20.dp),
        text = alerts,
        color = androidx.compose.ui.graphics.Color.Black,
    )
}

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
@Composable
private fun CurrentWeatherWidget(modifier: Modifier, weatherConditionAtMapCenter: Flow<WeatherCondition>) {
    val conditions = weatherConditionAtMapCenter.collectAsState(null).value ?: return

    val unit = when (conditions.fields.systemOfMeasurement) {
        WeatherSystemOfMeasurement.Imperial -> "F"
        WeatherSystemOfMeasurement.Metric -> "C"
        else -> "C"
    }

    Box(
        modifier = modifier
            .width(90.dp)
            .height(90.dp)
            .shadow(8.dp, shape = CircleShape)
            .background(androidx.compose.ui.graphics.Color.White),
    ) {
        Image(
            modifier = Modifier
                .align(BiasAlignment(horizontalBias = 0.4f, verticalBias = 0.4f))
                .size(40.dp),
            colorFilter = ColorFilter.tint(androidx.compose.ui.graphics.Color.Black),
            painter = painterResource(conditions.fields.iconCode.toIcon()),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.align(
                BiasAlignment(horizontalBias = -0.3f, verticalBias = -0.3f),
            ),
            text = "${conditions.fields.temperature?.toInt()} °$unit",
            maxLines = 1,
            color = androidx.compose.ui.graphics.Color.Black,
        )
    }
}
