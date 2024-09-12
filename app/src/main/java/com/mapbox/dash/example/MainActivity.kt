package com.mapbox.dash.example

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.commitNow
import com.google.android.material.slider.Slider
import com.mapbox.dash.compose.ComposeViewBlock
import com.mapbox.dash.compose.component.Body5
import com.mapbox.dash.destination.preview.places.DefaultPlacesPreview
import com.mapbox.dash.destination.preview.presentation.DefaultDestinationPreview
import com.mapbox.dash.destination.preview.presentation.DefaultRoutesOverview
import com.mapbox.dash.example.databinding.ActivityMainBinding
import com.mapbox.dash.example.databinding.LayoutCustomizationMenuBinding
import com.mapbox.dash.example.ui.SampleDestinationPreview
import com.mapbox.dash.example.ui.SamplePlacesView
import com.mapbox.dash.example.ui.SampleRoutesOverview
import com.mapbox.dash.example.ui.SampleTripSummaryView
import com.mapbox.dash.favorites.PlaceFavoriteStatus
import com.mapbox.dash.logging.LogsExtra
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.dash.sdk.config.api.DashSearchPanelButton
import com.mapbox.dash.sdk.config.api.DashSidebarControl
import com.mapbox.dash.sdk.config.api.PersonalLocations
import com.mapbox.dash.sdk.config.api.SearchCategory
import com.mapbox.dash.sdk.config.dsl.DashSidebarUpdate
import com.mapbox.dash.sdk.config.dsl.DashUiUpdate
import com.mapbox.dash.sdk.config.dsl.destinationPreview
import com.mapbox.dash.sdk.config.dsl.etaPanel
import com.mapbox.dash.sdk.config.dsl.leftSidebar
import com.mapbox.dash.sdk.config.dsl.mapStyle
import com.mapbox.dash.sdk.config.dsl.rightSidebar
import com.mapbox.dash.sdk.config.dsl.searchPanel
import com.mapbox.dash.sdk.config.dsl.theme
import com.mapbox.dash.sdk.config.dsl.ui
import com.mapbox.dash.sdk.config.dsl.uiSettings
import com.mapbox.dash.sdk.config.dsl.voices
import com.mapbox.dash.sdk.search.api.DashFavoriteType
import com.mapbox.dash.sdk.search.api.DashSearchResult
import com.mapbox.dash.sdk.search.api.DashSearchResultType
import com.mapbox.dash.theming.compose.AppTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : DrawerActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    private val menuBinding by lazy { LayoutCustomizationMenuBinding.inflate(LayoutInflater.from(this)) }

    private val themeVM: ThemeViewModel by viewModels()
    private val mapStyleVM: MapStyleViewModel by viewModels()


    private val headlessMode = MutableStateFlow(false)

    private val searchItem = buildSearchItem()
    private val favoriteItem = buildSearchItem()

    private fun buildSearchItem() =
        object : DashSearchResult {
            override val address = null
            override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
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
    private val setOfflineTts = MutableStateFlow(value = false)
    private val showRouteOptionsInSettings = MutableStateFlow(value = false)
    private val showSpeedLimitsOptionsInSettings = MutableStateFlow(value = false)
    private val leftSidebarMode = MutableStateFlow(SidebarMode.Transparent.name)
    private val rightSidebarMode = MutableStateFlow(SidebarMode.Transparent.name)
    private val overrideSidebarControls = MutableStateFlow(value = false)
    private val searchPanelPosition = MutableStateFlow(SearchPanelPosition.BottomLeft.name)
    private val overrideSearchPanelButtons = MutableStateFlow(value = false)
    private val setCustomMarkerFactory = MutableStateFlow(value = false)
    private val setCustomPlacesListComposer = MutableStateFlow(value = false)
    private val setCustomDestinationPreviewComposer = MutableStateFlow(value = false)
    private val setCustomRoutesOverviewComposer = MutableStateFlow(value = false)
    private val setCustomTripSummaryComposer = MutableStateFlow(value = false)
    private val showTripProgress = MutableStateFlow(value = true)
    private val setCustomDestination = MutableStateFlow(value = true)
    private val simpleCardHeader = MutableStateFlow(value = false)
    private val setCustomVoicePlayer = MutableStateFlow(value = false)

    private fun initCustomizationMenu() {
        headlessModeCustomization()
        logsCustomization()
        themeCustomization()
        mapStyleCustomization()
        settingCustomization()
        offlineTtsCustomization()
        dashCoordination()
        etaPanelCustomization()
        destinationPreviewCustomization()
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
        menuBinding.themeSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, themes).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        menuBinding.themeSpinner.setSelection(themes.indexOf(themeVM.dashTheme.value))
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

        val mapStyleLightingNames = mapStyleVM.mapStyleLightingNames
        val lightingAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mapStyleLightingNames)
        lightingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        menuBinding.mapStyleLighting.adapter = lightingAdapter
        bindSpinner(
            spinner = menuBinding.mapStyleLighting,
            state = mapStyleVM.mapStyleLighting,
            onSelected = { lighting ->
                Dash.applyUpdate {
                    ui {
                        mapStyleLighting = lighting
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

    private fun destinationPreviewCustomization() {
        bindSwitch(
            switch = menuBinding.setCustomDestinationPreview,
            state = setCustomDestination,
        ) { isChecked ->
            setCustomDestination.observeWhenStarted(this) {
                if (isChecked) {
                    getDashNavigationFragment()?.setSingleCustomView()
                } else {
                    getDashNavigationFragment()?.editLayout {
                        defaultDestinationPreviewLayout()
                    }
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
            Dash.controller.addFavoriteItem(favoriteItem, DashFavoriteType.REGULAR)
        }
        menuBinding.removeFromFavorites.bindAction {
            Dash.controller.removeFavoriteItem(favoriteItem)
        }
        menuBinding.btnSearchApi.bindAction {
            Dash.controller.search(menuBinding.etSearchApi.text.toString())
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
                                        Text(
                                            modifier = modifier
                                                .shadow(8.dp, shape = CircleShape)
                                                .border(
                                                    width = 2.dp,
                                                    color = androidx.compose.ui.graphics.Color.Black,
                                                    shape = CircleShape,
                                                )
                                                .background(androidx.compose.ui.graphics.Color.White)
                                                .clickable {
                                                    Toast
                                                        .makeText(
                                                            this@MainActivity,
                                                            "This text was brought to you by Dash",
                                                            Toast.LENGTH_LONG,
                                                        )
                                                        .show()
                                                }
                                                .padding(all = 20.dp),
                                            text = "Custom sidebar button",
                                            color = androidx.compose.ui.graphics.Color.Black,
                                        )
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
                        visible = position != SearchPanelPosition.Nowhere
                        this.position = if (position == SearchPanelPosition.TopLeft) {
                            com.mapbox.dash.sdk.config.api.SearchPanelPosition.TOP_LEFT
                        } else {
                            com.mapbox.dash.sdk.config.api.SearchPanelPosition.BOTTOM_LEFT
                        }
                    }
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleSearchPanelButtons,
            state = overrideSearchPanelButtons,
        ) { enabled ->
            Dash.applyUpdate {
                ui {
                    searchPanel {
                        buttons = if (enabled) {
                            listOf(
                                DashSearchPanelButton.Category(SearchCategory.Airport),
                                DashSearchPanelButton.Favorite(PersonalLocations.Home),
                                DashSearchPanelButton.Custom(
                                    id = "hello",
                                    iconId = R.drawable.ic_waving_hand,
                                    onClick = {
                                        Toast.makeText(this@MainActivity, "Hey, Dash!", Toast.LENGTH_SHORT).show()
                                    },
                                ),
                            )
                        } else {
                            DashSearchPanelButton.defaultSearchPanelButtons
                        }
                    }
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
                        SampleDestinationPreview(modifier, state)
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
                        SampleTripSummaryView(modifier, tripSummaryUiState)
                    }
                } else {
                    fragment.setTripSummary(null)
                }
            }
        }

        bindSwitch(
            switch = menuBinding.toggleCustomVoicePlayer,
            state = setCustomVoicePlayer,
        ) { enabled ->
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
                    println(">> Search result [$index]: " +
                            "name = ${result.name}, " +
                            "eta = ${result.etaMinutes}, " +
                            "distance = ${result.distanceMeters}")
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
    }

    internal enum class CustomDashTheme(
        val dayResId: Int,
        val nightResId: Int,
    ) {

        DEFAULT(
            com.mapbox.dash.themes.R.style.DashTheme_Day,
            com.mapbox.dash.themes.R.style.DashTheme_Night
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

    @SuppressLint("RestrictedApi")
    private suspend fun DashNavigationFragment.setSingleCustomView() = editLayout {
        updateDestinationPreviewLayout {
            assign {
                arrivalInformation(LoadingViewBlock)
                weather(LoadingViewBlock)
                rating(LoadingViewBlock)
                openHours(LoadingViewBlock)
                chartingInformation(LoadingViewBlock)
                destinationInformation(LoadingViewBlock)
                destinationFeedback(LoadingViewBlock)
            }
            delay(1_000)
            assign {
                destinationInformation(ComposeViewBlock {
                    val coroutine = rememberCoroutineScope()
                    Row(
                        modifier = Modifier.padding(top = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(id = AppTheme.icons.information.pin),
                            contentDescription = null,
                        )
                        Body5(
                            modifier = Modifier.weight(1f),
                            text = "Address: ${it.address.orEmpty()}",
                            color = AppTheme.colors.textColor.primary,
                        )
                        Image(
                            modifier = Modifier
                                .size(54.dp)
                                .border(
                                    width = 1.dp,
                                    color = AppTheme.colors.borderColors.primary,
                                    shape = CircleShape,
                                )
                                .clip(CircleShape)
                                .clickable {
                                    coroutine.launch {
                                        if (it.favoriteStatus == PlaceFavoriteStatus.NOT_A_FAVORITE) {
                                            Dash.controller.addFavoriteItem(it.origin, DashFavoriteType.REGULAR)
                                        } else {
                                            Dash.controller.removeFavoriteItem(it.origin)
                                        }
                                    }
                                }
                                .padding(all = 12.dp),
                            painter = painterResource(
                                id = when (it.favoriteStatus) {
                                    PlaceFavoriteStatus.HOME -> AppTheme.icons.main.home
                                    PlaceFavoriteStatus.WORK -> AppTheme.icons.main.work
                                    PlaceFavoriteStatus.REGULAR -> AppTheme.icons.main.favorite
                                    PlaceFavoriteStatus.NOT_A_FAVORITE -> AppTheme.icons.main.addFavorite
                                },
                            ),
                            colorFilter = ColorFilter.tint(
                                color = when (it.favoriteStatus) {
                                    PlaceFavoriteStatus.HOME,
                                    PlaceFavoriteStatus.WORK,
                                    -> AppTheme.colors.iconColor.accent

                                    PlaceFavoriteStatus.REGULAR -> AppTheme.colors.iconColor.red
                                    PlaceFavoriteStatus.NOT_A_FAVORITE -> AppTheme.colors.iconColor.secondary
                                },
                            ),
                            contentDescription = null,
                        )
                    }
                })
            }
        }
    }

    private enum class SidebarMode {
        Transparent, Background, Hidden,
    }

    private enum class SearchPanelPosition {
        BottomLeft, TopLeft, Nowhere,
    }

    private companion object {

        private const val TAG = "MainActivity"

        private var tabletLayout: Boolean? = null
        private var densityDpi: Int? = null

        /**
         * Default 3D style.
         */
        private const val DEFAULT_3D_STYLE = "mapbox://styles/mapbox-dash/standard-navigation"
    }
}
