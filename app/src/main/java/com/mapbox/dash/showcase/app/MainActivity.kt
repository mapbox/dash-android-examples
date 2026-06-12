package com.mapbox.dash.showcase.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
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
import com.mapbox.dash.sdk.config.api.MapStyleMode
import com.mapbox.dash.sdk.config.api.MapStyleTheme
import com.mapbox.dash.sdk.config.api.ScreenDirectionality
import com.mapbox.dash.sdk.config.api.SearchPanelPosition
import com.mapbox.dash.sdk.config.api.ThemeFactory
import com.mapbox.dash.sdk.config.api.UiModeSettings
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
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.dash.sdk.search.api.DashSearchResultType
import com.mapbox.dash.sdk.storage.ExternalProfile
import com.mapbox.dash.showcase.app.menu.LayersManagerCard
import com.mapbox.dash.showcase.app.menu.MenuButton
import com.mapbox.dash.showcase.app.menu.MenuDropDown
import com.mapbox.dash.showcase.app.menu.MenuEditText
import com.mapbox.dash.showcase.app.menu.MenuSlider
import com.mapbox.dash.showcase.app.menu.MenuSwitch
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
import com.mapbox.dash.state.defaults.camera.FollowingDefaults
import com.mapbox.dash.theming.Theme
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.navigation.audio.text.TTS_PROVIDER_CORE
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.mapgpt.LottieMapGptAvatar
import com.mapbox.navigation.mapgpt.core.textplayer.TTS_PROVIDER_MAP_GPT
import com.mapbox.navigation.mapgpt.mapGpt
import com.mapbox.navigation.mapgpt.mapGptCompose
import com.mapbox.navigation.mapgpt.setDefaultVoicePlayerMiddleware
import com.mapbox.navigation.mapgpt.setVoicePlayerMiddleware
import com.mapbox.navigation.mapgpt.stopMapGptConversation
import com.mapbox.navigation.mapgpt.ui.MapGptCarouselCardParams
import com.mapbox.navigation.mapgpt.updateMapGptContextOverrides
import com.mapbox.navigation.mapgpt.useroutput.PrebuiltMapGptAvatars
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@Suppress("MagicNumber", "LargeClass")
@OptIn(
    ExperimentalCoroutinesApi::class, ExperimentalPreviewMapboxNavigationAPI::class,
    MapboxExperimental::class, FlowPreview::class,
)
class MainActivity : DrawerActivity() {

    private val mapGptVM by viewModels<MapGptViewModel>()
    private val layoutVM by viewModels<LayoutViewModel>()
    private val evViewModel by viewModels<EvViewModel>()

    private val fragmentFlow by lazy {
        callbackFlow {
            val callbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentCreated(fm: FragmentManager, fragment: Fragment, savedInstanceState: Bundle?) {
                    trySend(fragment)
                }
            }
            supportFragmentManager.registerFragmentLifecycleCallbacks(callbacks, false)
            awaitClose { supportFragmentManager.unregisterFragmentLifecycleCallbacks(callbacks) }
        }.stateIn(lifecycleScope, SharingStarted.Eagerly, supportFragmentManager.findFragmentById(R.id.container))
    }

    private val dashNavigationFragmentFlow by lazy {
        fragmentFlow.map { it as? DashNavigationFragment }
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
        override val routablePoints = null
    }

    private fun getDashNavigationFragment(): DashNavigationFragment? {
        return fragmentFlow.value as? DashNavigationFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val newFragment = if (BuildConfig.HEADLESS_MODE_ENABLED) {
                HeadlessModeFragment.newInstance()
            } else {
                DashNavigationFragment.newInstance()
            }
            supportFragmentManager.commit { replace(R.id.container, newFragment) }
        }

        initCustomizationMenu()
        registerEventsObservers()

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

    override fun onStop() {
        super.onStop()
        Dash.controller.stopMapGptConversation()
    }

    @Composable
    override fun MenuView(modifier: Modifier) {
        Column(modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(android.R.color.holo_blue_dark))
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
                    .padding(8.dp),
                text = "DASH Customizations",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
            MenuSwitch(
                text = "HEADLESS MODE",
                flow = fragmentFlow,
                transform = { it is HeadlessModeFragment },
                onCheckedChange = { enabled ->
                    val currentFragment = fragmentFlow.value
                    val newFragment = if (enabled) {
                        if (currentFragment is HeadlessModeFragment) return@MenuSwitch
                        HeadlessModeFragment.newInstance()
                    } else {
                        if (currentFragment is DashNavigationFragment) return@MenuSwitch
                        DashNavigationFragment.newInstance()
                    }
                    supportFragmentManager.commit { replace(R.id.container, newFragment) }
                },
            )
            MenuSwitch(
                text = "SIMULATE LOCATION",
                initial = false,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        locationSimulation {
                            this.locationSimulationEnabled = enabled
                        }
                    }
                },
            )
            MenuButton(
                text = "RESET NAVIGATION STATE",
                backgroundId = android.R.color.holo_red_dark,
                onClick = {
                    Dash.controller.resetNavigationState(
                        // Reset the navigation state to the Free Drive mode
                        // and show the route recovery prompt
                        shouldShowRouteRecoveryPrompt = true,
                    )
                },
            )
            MenuSwitch(
                text = "CONNECT MAPBOX STACK",
                initial = true,
                onCheckedChange = { isConnected ->
                    Dash.applyUpdate {
                        network { isMapboxStackConnected = isConnected }
                    }
                },
            )
            MenuSwitch(
                text = "ENABLE DEBUG LOGCAT",
                initial = true,
                onCheckedChange = { isChecked ->
                    Dash.applyUpdate {
                        logLevel = if (isChecked) LogsExtra.LOG_LEVEL_DEBUG else LogsExtra.LOG_LEVEL_INFO
                    }
                },
            )
            MenuDropDown(
                options = CustomDashTheme.entries.map { it.name },
                initial = CustomDashTheme.CUSTOM.name,
                onValueChange = { name ->
                    Dash.applyUpdate {
                        theme {
                            themeFactory = CustomDashTheme.valueOf(name).themeFactory
                        }
                    }
                },
                label = "Dash Theme",
            )
            MenuDropDown(
                options = listOf(
                    MapLayer.Default.name, MapLayer.Custom.name,
                    MapLayer.WeatherAlongRoute.name, MapLayer.EvChargePoint.name,
                ),
                initial = MapLayer.Default.name,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onValueChange = { dashNavigationFragment, mapLayer ->
                    when (MapLayer.valueOf(mapLayer)) {
                        MapLayer.Default -> dashNavigationFragment.setMapLayer(DefaultMapLayerComposer)
                        MapLayer.Custom -> dashNavigationFragment.setMapLayer {
                            middleSlot {
                                CustomLayerBlock()
                            }

                            topSlot {
                                WeatherLayer()
                            }
                        }
                        MapLayer.WeatherAlongRoute -> dashNavigationFragment.setMapLayer {
                            topSlot {
                                WeatherAlongRouteBlock(weatherController.weatherWarningsAlongRoute) { message ->
                                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        MapLayer.EvChargePoint -> dashNavigationFragment.setMapLayer {
                            topSlot {
                                EvChargePointBlock(evViewModel.chargePoints) { message ->
                                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                label = "Map Layer",
            )
            LayersManagerCard(dashNavigationFragmentFlow)
            MenuSwitch(
                text = "ENABLE MAPGPT",
                initial = BuildConfig.MAP_GPT_ENABLED,
                onCheckedChange = { isChecked ->
                    Dash.applyUpdate {
                        mapGpt {
                            isEnabled = isChecked
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SHOW DEFAULT AVATAR",
                flow = mapGptCompose.config,
                transform = { it.showAvatar },
                onCheckedChange = { isChecked ->
                    mapGptCompose.config.update { config ->
                        config.build { showAvatar = isChecked }
                    }
                },
            )
            MenuSwitch(
                text = "SHOW DEFAULT KEYBOARD MODE",
                flow = mapGptCompose.config,
                transform = { it.showKeyboardMode },
                onCheckedChange = { isChecked ->
                    mapGptCompose.config.update { config ->
                        config.build { showKeyboardMode = isChecked }
                    }
                },
            )
            MenuSwitch(
                text = "SHOW DEFAULT CAROUSEL",
                flow = mapGptCompose.config,
                transform = { it.showCarousel },
                onCheckedChange = { isChecked ->
                    mapGptCompose.config.update { config ->
                        config.build { showCarousel = isChecked }
                    }
                },
            )
            MenuSwitch(
                text = "SHOW DEFAULT CHAT BUBBLE",
                flow = mapGptCompose.config,
                transform = { it.showChatBubble },
                onCheckedChange = { isChecked ->
                    mapGptCompose.config.update { config ->
                        config.build { showChatBubble = isChecked }
                    }
                },
            )
            MenuSwitch(
                text = "CUSTOM CHAT BUBBLE",
                state = mapGptVM.mapGptCustomChatBubble,
            )
            MenuSwitch(
                text = "CUSTOM MAP GPT CAROUSEL CARD PARAMS",
                flow = mapGptCompose.mapGptCarouselCardParams,
                transform = { it != null },
                onCheckedChange = { isChecked ->
                    mapGptCompose.mapGptCarouselCardParams.value = if (isChecked) {
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
            MenuDropDown(
                options = sampleAvatars.keys.toList(),
                flow = mapGptCompose.config,
                transform = { it.avatar?.name ?: UNSET_VALUE },
                onValueChange = { avatarName ->
                    mapGptCompose.config.update { config ->
                        config.build { avatar = sampleAvatars[avatarName] }
                    }
                },
                label = "MapGPT Avatar",
            )
            MenuSwitch(
                text = "SET 3D MAP STYLE",
                initial = true,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        mapStyle {
                            map3dStyleUri = if (enabled) DEFAULT_3D_STYLE else ""
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET NIGHT MAP STYLE",
                initial = true,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        mapStyle {
                            nightStyleUri = if (enabled) ShowcaseApp.NIGHT_MAP_STYLE else ""
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET SATELLITE MAP STYLE",
                initial = true,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        mapStyle {
                            satelliteStyleUri = if (enabled) ShowcaseApp.SATELLITE_MAP_STYLE else ""
                        }
                    }
                },
            )
            MenuSwitch(
                text = "OFFLINE TTS",
                initial = false,
                onCheckedChange = { setOfflineTts ->
                    Dash.applyUpdate {
                        voices {
                            preferLocalTts = setOfflineTts
                        }
                    }
                },
            )
            MenuSwitch(
                text = "Custom TextPlayerMiddleware",
                initial = false,
                onCheckedChange = { useCustomVoicePlayerMiddleware ->
                    if (useCustomVoicePlayerMiddleware) {
                        Dash.controller.setVoicePlayerMiddleware(LocalVoicePlayerMiddleware())
                    } else {
                        Dash.controller.setDefaultVoicePlayerMiddleware()
                    }
                },
            )
            MenuDropDown(
                options = listOf(TTS_PROVIDER_CORE, TTS_PROVIDER_MAP_GPT),
                initial = TTS_PROVIDER_CORE,
                onValueChange = { provider ->
                    Dash.applyUpdate {
                        voices { remoteTtsProvider = provider }
                    }
                },
                label = "RemoteTtsProvider",
            )
            MenuSwitch(
                text = "SETTINGS: SHOW ROUTE OPTIONS",
                initial = true,
                onCheckedChange = { showRouteOptions ->
                    Dash.applyUpdate {
                        uiSettings {
                            this.showRouteOptions = showRouteOptions
                        }
                    }
                },
            )
            MenuSwitch(
                text = "ROUTE OPTION: AVOID HIGHWAYS",
                initial = true,
                onCheckedChange = { avoidHighways ->
                    Dash.applyUpdate {
                        routeOptions {
                            this.avoidHighways = avoidHighways
                        }
                    }
                },
            )
            MenuSwitch(
                text = "ROUTE OPTION: AVOID TOLLS",
                initial = true,
                onCheckedChange = { avoidTolls ->
                    Dash.applyUpdate {
                        routeOptions {
                            this.avoidTolls = avoidTolls
                        }
                    }
                },
            )
            MenuSwitch(
                text = "ROUTE OPTION: AVOID FERRIES",
                initial = true,
                onCheckedChange = { avoidFerries ->
                    Dash.applyUpdate {
                        routeOptions {
                            this.avoidFerries = avoidFerries
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SETTINGS: SHOW SPEED LIMITS OPTIONS",
                initial = true,
                onCheckedChange = { showSpeedLimitsOptions ->
                    Dash.applyUpdate {
                        uiSettings {
                            this.showSpeedLimitsOptions = showSpeedLimitsOptions
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SETTINGS: SHOW PREFERRED NETWORKS",
                initial = false,
                onCheckedChange = { showPreferredNetworks ->
                    Dash.applyUpdate {
                        uiSettings {
                            this.showPreferredNetworks = showPreferredNetworks
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SETTINGS: SHOW LOCAL TTS",
                initial = false,
                onCheckedChange = { showLocalTtsOptions ->
                    Dash.applyUpdate {
                        uiSettings {
                            this.showLocalTtsOptions = showLocalTtsOptions
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET NAVIGATION SUGGESTIONS",
                initial = false,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        combine(
                            Dash.controller.observeFavorites(),
                            Dash.controller.observeHistory(),
                        ) { favorites, history -> favorites + history }
                            .collect { records ->
                                val suggestions = records.map { DashNavigationSuggestion(it) }
                                Dash.controller.setNavigationSuggestions(suggestions)
                            }
                    } else {
                        Dash.controller.setNavigationSuggestions(emptyList())
                    }
                },
            )
            MenuSwitch(
                text = "SET REVERSE UI MODE",
                initial = false,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        ui {
                            uiModeMapper = if (enabled) ReversedUiModeMapper else DefaultUiModeMapper
                        }
                    }
                },
            )
            MenuSwitch(
                text = "LEFT HAND TRAFFIC LAYOUT",
                initial = false,
                onCheckedChange = { useLeftHandTrafficLayout ->
                    Dash.applyUpdate {
                        ui {
                            screenDirectionality = if (useLeftHandTrafficLayout) {
                                ScreenDirectionality.RIGHT_TO_LEFT
                            } else {
                                ScreenDirectionality.LEFT_TO_RIGHT
                            }
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SHOVE GESTURE ENABLED",
                initial = false,
                onCheckedChange = { shoveGestureEnabled ->
                    Dash.applyUpdate {
                        camera {
                            isShoveGestureEnabled = shoveGestureEnabled
                        }
                    }
                },
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Coordination api",
            )
            MenuButton(
                text = "SET DESTINATION",
                onClick = {
                    val location = Dash.controller.observeRawLocation().first()
                    val destination = location.getRandomDestinationAround()
                    getDashNavigationFragment()?.setDestination(destination)
                },
            )
            MenuButton(
                text = "START DESTINATION ROUTE",
                onClick = { Dash.controller.startNavigation(0) },
            )
            MenuButton(
                text = "STOP NAVIGATION",
                onClick = { Dash.controller.stopNavigation() },
            )
            MenuButton(
                text = "NAVIGATE NEXT LEG",
                onClick = { Dash.controller.navigateNextRouteLeg() },
            )
            MenuButton(
                text = "SHOW EV RANGE MAP",
                onClick = { Dash.controller.showEvRangeMap() },
            )
            MenuButton(
                text = "HIDE EV RANGE MAP",
                onClick = { Dash.controller.hideEvRangeMap() },
            )
            val fullScreenSearchQuery = rememberSaveable { mutableStateOf("") }
            MenuEditText(
                state = fullScreenSearchQuery,
                hint = "FullScreen search query",
            )
            MenuButton(
                text = "OPEN SEARCH",
                onClick = {
                    closeDrawers()
                    val query = fullScreenSearchQuery.value
                    val newYork = Point.fromLngLat(-73.98200596982161, 40.72726118601179)
                    getDashNavigationFragment()?.openSearch(query, newYork)
                },
            )
            MenuButton(
                text = "CLOSE SEARCH",
                onClick = {
                    closeDrawers()
                    getDashNavigationFragment()?.closeSearch()
                },
            )
            MenuButton(
                text = "SEARCH ON MAP",
                onClick = {
                    closeDrawers()
                    val query = fullScreenSearchQuery.value
                    getDashNavigationFragment()?.openMapWithSearch(query)
                },
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "History api",
            )
            MenuButton(
                text = "CLEAN HISTORY",
                onClick = { Dash.controller.cleanHistory() },
            )
            MenuButton(
                text = "ADD TO HISTORY",
                onClick = { Dash.controller.addHistoryItem(searchItem) },
            )
            MenuButton(
                text = "REMOVE FROM HISTORY",
                onClick = { Dash.controller.removeHistoryItem(searchItem) },
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Favorites api",
            )
            MenuButton(
                text = "ADD TO FAVORITES",
                onClick = { Dash.controller.addFavoriteItem(searchItem, DashFavoriteType.HOME) },
            )
            MenuButton(
                text = "REMOVE FROM FAVORITES",
                onClick = { Dash.controller.removeFavoriteItem(searchItem, DashFavoriteType.HOME) },
            )
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Search api",
            )
            val searchApiQuery = rememberSaveable { mutableStateOf("") }
            MenuEditText(
                state = searchApiQuery,
                hint = "Search api query",
            )
            MenuButton(
                text = "SEARCH QUERY",
                onClick = {
                    // simulate 2 sequentially requests. The first one should be canceled and
                    // Dash.controller.observeSearchRequestStatus() should provide a valid status.
                    // delay(200) is needed to give enough time to start a request,
                    // otherwise it will be canceled immediately.
                    delay(200)
                    performSearch("aaa")
                    delay(200)
                    performSearch(searchApiQuery.value)
                },
            )
            MenuButton(
                text = "OPEN MAP WITH CINEMA",
                onClick = {
                    getDashNavigationFragment()?.openMapWithCategorySearch("cinema", "Cinema")
                    closeDrawers()
                },
            )
            MenuSwitch(
                text = "CUSTOM SEARCH RESULTS",
                state = ShowcaseSearchResultsAdapter.enabled,
            )
            MenuDropDown(
                options = ShowcaseSearchEngine.allPolicies,
                state = ShowcaseSearchEngine.selectedPolicyName,
                label = "Custom search engine policy",
            )
            MenuDropDown(
                options = CustomLocationPuck.names(),
                initial = CustomLocationPuck.DEFAULT.name,
                onValueChange = { selection ->
                    val puck = CustomLocationPuck.valueOf(selection).getLocationPuck(this@MainActivity)
                    Dash.applyUpdate {
                        theme {
                            locationPuck = puck
                        }
                    }
                },
                label = "Location Puck",
            )
            val freeDrivePitch = rememberSaveable { mutableFloatStateOf(0f) }
            MenuSlider(
                state = freeDrivePitch,
                valueRange = 0f..70f,
                label = "FREE DRIVE PITCH",
            )
            LaunchedEffect(freeDrivePitch.value) {
                Dash.applyUpdate {
                    camera {
                        val pitch = freeDrivePitch.value.toDouble()
                        freeDriveDefaults = FollowingDefaults(freeDriveDefaults.zoom, pitch)
                    }
                }
            }
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = "Safe area",
            )
            val leftPadding = rememberSaveable { mutableFloatStateOf(0f) }
            MenuSlider(
                state = leftPadding,
                valueRange = 0f..100f,
                label = "LEFT PADDING",
            )
            val topPadding = rememberSaveable { mutableFloatStateOf(0f) }
            MenuSlider(
                state = topPadding,
                valueRange = 0f..100f,
                label = "TOP PADDING",
            )
            val rightPadding = rememberSaveable { mutableFloatStateOf(0f) }
            MenuSlider(
                state = rightPadding,
                valueRange = 0f..100f,
                label = "RIGHT PADDING",
            )
            val bottomPadding = rememberSaveable { mutableFloatStateOf(0f) }
            MenuSlider(
                state = bottomPadding,
                valueRange = 0f..100f,
                label = "BOTTOM PADDING",
            )
            val density = LocalDensity.current
            LaunchedEffect(density, leftPadding.value, topPadding.value, rightPadding.value, bottomPadding.value) {
                with(density) {
                    dashNavigationFragmentFlow.filterNotNull().collect { dashNavigationFragment ->
                        dashNavigationFragment.setSafeAreaPaddings(
                            leftPadding.value.dp.roundToPx(), topPadding.value.dp.roundToPx(),
                            rightPadding.value.dp.roundToPx(), bottomPadding.value.dp.roundToPx(),
                        )
                    }
                }
            }
            MenuSwitch(
                text = "OVERRIDE SIDEBAR CONTROLS",
                state = layoutVM.overrideSidebarControls,
            )
            MenuDropDown(
                options = listOf(SearchPanelPosition.BOTTOM_LEFT, SearchPanelPosition.TOP_LEFT),
                initial = SearchPanelPosition.BOTTOM_LEFT,
                onValueChange = { position ->
                    Dash.applyUpdate {
                        ui {
                            searchPanel {
                                this.position = position
                            }
                        }
                    }
                },
                label = "Search panel position",
            )
            MenuSwitch(
                text = "ENABLE CUSTOM MARKER FACTORY",
                initial = false,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        mapStyle {
                            markerFactory = if (enabled) {
                                SampleMarkerFactory(this@MainActivity)
                            } else {
                                null
                            }
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM SEARCH PANEL",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setSearchPanel { modifier, searchPanelState ->
                            SampleSearchPanel(modifier, searchPanelState, fragment)
                        }
                    } else {
                        fragment.setSearchPanel { modifier, searchPanelState ->
                            DefaultSearchPanelView(modifier, searchPanelState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM RECENTER CAMERA BUTTON",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
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
                },
            )
            MenuSwitch(
                text = "SET CUSTOM PLACES LIST",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setPlacesPreview { state, modifier ->
                            SamplePlacesView(state, modifier)
                        }
                    } else {
                        fragment.setPlacesPreview { state, modifier ->
                            DefaultPlacesPreview(state, modifier)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM DESTINATION PREVIEW",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setDestinationPreview { modifier, state ->
                            SampleDestinationPreview(modifier, state, weatherController)
                        }
                    } else {
                        fragment.setDestinationPreview { modifier, _ ->
                            DefaultDestinationPreview(modifier)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM GUIDANCE BANNER",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setManeuver { modifier, state ->
                            SampleGuidanceBanner(modifier, state)
                        }
                    } else {
                        fragment.setManeuver { modifier, state ->
                            DefaultManeuverView(modifier, state)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM UPCOMING MANEUVERS",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setUpcomingManeuvers { modifier, state ->
                            SampleUpcomingManeuversBanner(modifier, state)
                        }
                    } else {
                        fragment.setUpcomingManeuvers { modifier, state ->
                            DefaultUpcomingManeuversView(modifier, state)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM ROUTES OVERVIEW",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setRoutesOverview { modifier, routesOverviewState, backCloseButtonState ->
                            SampleRoutesOverview(modifier, routesOverviewState, backCloseButtonState)
                        }
                    } else {
                        fragment.setRoutesOverview { modifier, routesOverviewState, backCloseButtonState ->
                            DefaultRoutesOverview(modifier, routesOverviewState, backCloseButtonState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM TRIP SUMMARY",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setTripSummary { modifier, tripSummaryUiState ->
                            SampleTripSummaryView(modifier, tripSummaryUiState, weatherController)
                        }
                    } else {
                        fragment.setTripSummary { modifier, tripSummaryUiState ->
                            DefaultTripSummary(modifier, tripSummaryUiState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM END ACTIVE GUIDANCE",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setEndActiveGuidance { modifier, uiState ->
                            SampleEndActiveGuidance(modifier, uiState)
                        }
                    } else {
                        fragment.setEndActiveGuidance { modifier, uiState ->
                            DefaultEndActiveGuidanceView(modifier, uiState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM EDIT TRIP",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setEditTrip { _, state ->
                            SampleEditTrip(state)
                        }
                    } else {
                        fragment.setEditTrip { modifier, state ->
                            DefaultEditTripCard(modifier, state)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM ARRIVAL FEEDBACK",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setArrivalFeedback { modifier, state ->
                            SampleArrivalView(modifier, state)
                        }
                    } else {
                        fragment.setArrivalFeedback { modifier, state ->
                            DefaultArrivalFeedbackView(modifier, state)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM CONTINUE NAVIGATION",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setContinueNavigation { modifier, state ->
                            SampleContinueNavigation(modifier, state)
                        }
                    } else {
                        fragment.setContinueNavigation { modifier, state ->
                            DefaultContinueNavigationView(modifier, state)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM DRIVER NOTIFICATION",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setDriverNotification { modifier, uiState ->
                            SampleDriverNotificationView(modifier, uiState)
                        }
                    } else {
                        fragment.setDriverNotification { modifier, uiState ->
                            DefaultDriverNotificationView(modifier, uiState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM SEARCH SCREEN",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setFullScreenSearch { modifier, setFullScreenSearch ->
                            SampleFullScreenSearch(modifier, setFullScreenSearch)
                        }
                        fragment.setFavoritesScreen { modifier, favoritesScreenState ->
                            SampleFavoritesScreen(modifier, favoritesScreenState)
                        }
                        fragment.setEditFavoriteScreen { modifier, editFavoriteScreenState ->
                            SampleEditFavoriteScreen(modifier, editFavoriteScreenState)
                        }
                    } else {
                        fragment.setFullScreenSearch { modifier, fullScreenSearchState ->
                            DefaultFullScreenSearch(modifier, fullScreenSearchState)
                        }
                        fragment.setFavoritesScreen { modifier, favoritesScreenState ->
                            DefaultFavoritesScreen(modifier, favoritesScreenState)
                        }
                        fragment.setEditFavoriteScreen { modifier, editFavoriteScreenState ->
                            DefaultEditFavoriteScreen(modifier, editFavoriteScreenState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM OFFLINE ALERT",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setOfflineRouteAlert { modifier, offlineRouteAlertState ->
                            SampleOfflineRouteAlert(modifier, offlineRouteAlertState)
                        }
                    } else {
                        fragment.setOfflineRouteAlert { modifier, offlineRouteAlertState ->
                            DefaultOfflineRouteAlert(modifier, offlineRouteAlertState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM RESUME GUIDANCE VIEW",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setResumeGuidanceView { modifier, resumeGuidanceViewState ->
                            SampleResumeGuidanceView(modifier, resumeGuidanceViewState)
                        }
                    } else {
                        fragment.setResumeGuidanceView { modifier, resumeGuidanceViewState ->
                            DefaultResumeGuidanceView(modifier, resumeGuidanceViewState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM STREET NAME LABEL",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setStreetNameLabel { modifier, uiState ->
                            SampleStreetName(modifier, uiState)
                        }
                    } else {
                        fragment.setStreetNameLabel { modifier, uiState ->
                            DefaultStreetNameView(modifier, uiState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SET CUSTOM RANGE MAP INFO VIEW",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setRangeMapInfoView { modifier, state ->
                            SampleRangeMapInfoView(modifier, state)
                        }
                    } else {
                        fragment.setRangeMapInfoView { modifier, state ->
                            DefaultRangeMapInfoView(modifier, state)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "OVERRIDE ROUTE CALLOUTS",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, overrideRouteCallout ->
                    fragment.setRouteCallout { state ->
                        if (overrideRouteCallout) {
                            SampleRouteCalloutView(state, fragment)
                        } else {
                            DefaultRouteCalloutView(state)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "OVERRIDE SEARCH THIS AREA",
                initial = false,
                dashNavigationFragmentFlow = dashNavigationFragmentFlow,
                onCheckedChange = { fragment, enabled ->
                    if (enabled) {
                        fragment.setSearchArea { uiState ->
                            SampleSearchArea(uiState)
                        }
                    } else {
                        fragment.setSearchArea { uiState ->
                            DefaultSearchAreaButton(uiState)
                        }
                    }
                },
            )
            MenuSwitch(
                text = "ENABLE SIMPLE CARD HEADERS",
                initial = false,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        destinationPreview {
                            titleSingleLine = enabled
                        }
                        ui {
                            showCloseButtonInCards = !enabled
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SHOW LANE GUIDANCE FOR UPCOMING MANEUVERS",
                initial = false,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        ui {
                            maneuverView {
                                showUpcomingLaneGuidance = enabled
                            }
                        }
                    }
                },
            )
            MenuSwitch(
                text = "SHOW CONNECTIVITY PROBLEMS INFO",
                initial = false,
                onCheckedChange = { enabled ->
                    Dash.applyUpdate {
                        Log.d(TAG, "Update showConnectivityProblemsInfo: $enabled")
                        ui {
                            this.showOfflineModeInfo = enabled
                        }
                    }
                },
            )
            MenuSwitch(
                text = "USE CUSTOM COMPASS DATA INPUT",
                initial = false,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        sampleSensorEventManager?.compassData?.collect { compassData ->
                            Dash.controller.updateCompassData(compassData)
                        }
                    }
                },
                enabled = sampleSensorEventManager != null,
            )
            MenuDropDown(
                options = listOf(MapStyleMode.MODE_3D, MapStyleMode.MODE_2D, MapStyleMode.SATELLITE),
                initial = MapStyleMode.MODE_3D,
                onValueChange = { mode ->
                    Dash.applyUpdate {
                        ui {
                            mapStyleMode = mode
                        }
                    }
                },
                label = "MapStyle mode",
            )
            MenuDropDown(
                options = listOf(MapStyleTheme.DEFAULT, MapStyleTheme.FADED, MapStyleTheme.MONO),
                initial = MapStyleTheme.DEFAULT,
                onValueChange = { theme ->
                    Dash.applyUpdate {
                        ui {
                            mapStyleTheme = theme
                        }
                    }
                },
                label = "MapStyle theme",
            )
            MenuDropDown(
                options = listOf(
                    UiModeSettings.AUTO, UiModeSettings.SYSTEM, UiModeSettings.DAWN,
                    UiModeSettings.DAY, UiModeSettings.DUSK, UiModeSettings.NIGHT,
                ),
                initial = UiModeSettings.AUTO,
                onValueChange = { mode ->
                    Dash.applyUpdate {
                        ui {
                            uiModeSettings = mode
                        }
                    }
                },
                label = "UI mode",
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val profile = produceState<ExternalProfile?>(initialValue = null) {
                    Dash.controller.observeExternalProfile().collect { value = it }
                }
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    text = stringResource(R.string.current_profile, profile.value?.id ?: UNSET_VALUE),
                )
                MenuButton(
                    modifier = Modifier.width(IntrinsicSize.Min),
                    text = "SWITCH",
                    onClick = {
                        showProfileAlert { profileId ->
                            Dash.controller.setExternalProfile(profileId?.let { ExternalProfile(it) })
                        }
                    },
                )
            }
            MenuButton(
                text = "REMOVE A PROFILE",
                onClick = {
                    showProfileAlert { profileId ->
                        lifecycleScope.launch {
                            Dash.controller.removeExternalProfile(profileId)
                        }
                    }
                },
            )
            MenuButton(
                text = "SHOW CURRENT ROUTES",
                onClick = { getDashNavigationFragment()?.showRoutesOverview() },
            )
            MenuDropDown(
                options = listOf(
                    EngineType.GAS, EngineType.PETROL, EngineType.DIESEL, EngineType.BIO_DIESEL,
                    EngineType.ELECTRIC, EngineType.HYDROGEN, EngineType.HYBRID,
                ),
                initial = EngineType.PETROL,
                onValueChange = { type ->
                    Dash.applyUpdate {
                        engineType = type
                    }
                },
                label = "Vehicle type",
            )
        }
    }

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
        repeatWhenStarted(lifecycleOwner = this) {
            combine(
                Dash.controller.observeEvRangeMapState(),
                dashNavigationFragmentFlow.filterNotNull(),
            ) { state, fragment ->
                fragment.setAdditionalPointsToFrame(state.rangeMapFramePoints)
            }.collect()
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

    private fun performSearch(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            Dash.controller.search(query)
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

    private enum class MapLayer {
        Default, Custom, WeatherAlongRoute, EvChargePoint,
    }

    private companion object {

        private const val TAG = "MainActivity"
        private const val UNSET_VALUE = "--"

        @SuppressLint("RestrictToUsage", "RestrictedApi")
        private val sampleAvatars = buildMap {
            putAll(PrebuiltMapGptAvatars.avatarMap)
            /**
             * This is demonstrating the ability to add a custom avatar with your own Lottie animations.
             */
            val smileBoxPeteAvatar = LottieMapGptAvatar(
                name = "SmileBoxPete",
                listeningToUser = com.mapbox.map.gpt.R.raw.ic_mapboxy_listening_to_user,
                userSpeaking = com.mapbox.map.gpt.R.raw.ic_petter_user_speaking,
                aiThinking = com.mapbox.map.gpt.R.raw.ic_smiley_thinking,
                aiSpeaking = com.mapbox.map.gpt.R.raw.ic_mapboxy_speaking,
                aiError = com.mapbox.map.gpt.R.raw.ic_smiley_error,
                aiIdle = com.mapbox.map.gpt.R.raw.ic_petter_listening_to_user,
                aiSleeping = com.mapbox.map.gpt.R.raw.ic_mapboxy_sleeping,
                noMicPermission = com.mapbox.map.gpt.R.raw.ic_smiley_no_mic_permission,
                serviceDisconnected = com.mapbox.map.gpt.R.raw.ic_petter_listening_to_user,
            )
            put(smileBoxPeteAvatar.name, smileBoxPeteAvatar)
            put(UNSET_VALUE, null)
        }
    }
}

internal enum class CustomDashTheme(
    val themeFactory: ThemeFactory,
) {

    DEFAULT(ThemeFactory(::Theme)),
    CUSTOM(CustomThemeFactory),
    RED(RedThemeFactory),
}
