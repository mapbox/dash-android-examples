package com.mapbox.dash.example

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatSpinner
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.google.android.material.slider.Slider
import com.mapbox.dash.example.databinding.ActivityMainBinding
import com.mapbox.dash.example.databinding.LayoutCustomizationMenuBinding
import com.mapbox.dash.logging.LogsExtra
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.DashNavigationFragment
import com.mapbox.dash.sdk.config.api.DashSearchPanelButton
import com.mapbox.dash.sdk.config.api.DashSidebarControl
import com.mapbox.dash.sdk.config.api.PersonalLocations
import com.mapbox.dash.sdk.config.api.SearchCategory
import com.mapbox.dash.sdk.config.dsl.DashSidebarUpdate
import com.mapbox.dash.sdk.config.dsl.DashUiUpdate
import com.mapbox.dash.sdk.config.dsl.leftSidebar
import com.mapbox.dash.sdk.config.dsl.mapStyle
import com.mapbox.dash.sdk.config.dsl.rightSidebar
import com.mapbox.dash.sdk.config.dsl.searchPanel
import com.mapbox.dash.sdk.config.dsl.theme
import com.mapbox.dash.sdk.config.dsl.ui
import com.mapbox.dash.sdk.config.dsl.uiSettings
import com.mapbox.dash.sdk.config.dsl.voices
import com.mapbox.dash.sdk.search.DashFavoriteType
import com.mapbox.dash.sdk.search.DashSearchResult
import com.mapbox.dash.sdk.search.DashSearchResultType
import com.mapbox.dash.theming.ThemeManager
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlin.math.roundToInt

class MainActivity : DrawerActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(LayoutInflater.from(this)) }
    private val menuBinding by lazy { LayoutCustomizationMenuBinding.inflate(LayoutInflater.from(this)) }

    private val themeVM: ThemeViewModel by viewModels()

    private val headlessMode = MutableStateFlow(false)

    private val searchItem = buildSearchItem()
    private val favoriteItem = buildSearchItem(DashFavoriteType.HOME)

    private fun buildSearchItem(@DashFavoriteType.Type favoriteType: String? = null) =
        object : DashSearchResult {
            override val address = null
            override val coordinate = Point.fromLngLat(-77.0342, 38.9044)
            override val etaMinutes = null
            override val id = "customHistoryItemId1122334455"
            override val mapboxId: String? = null
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

    private fun initCustomizationMenu() {
        headlessModeCustomization()
        logsCustomization()
        themeCustomization()
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
            Dash.controller.addFavoriteItem(favoriteItem)
        }
        menuBinding.removeFromFavorites.bindAction {
            Dash.controller.removeFavoriteItem(favoriteItem)
        }
        menuBinding.btnSearchApi.bindAction {
            Dash.controller.search(menuBinding.etSearchApi.text.toString())
        }

        val density = resources.displayMetrics.density
        val paddingSliderChangeListener = Slider.OnChangeListener { _, _, _ ->
            (supportFragmentManager.findFragmentById(R.id.container) as? DashNavigationFragment)?.setSafeAreaPaddings(
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
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "This text was brought to you by Dash",
                                                        Toast.LENGTH_LONG,
                                                    ).show()
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
                            ThemeManager.theme.backgroundColor.sidebar.color
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
        BottomLeft, TopLeft, Nowhere,
    }

    private companion object {

        const val TAG = "MainActivity"

        /**
         * Default satellite style.
         */
        const val DEFAULT_SATELLITE_STYLE = "mapbox://styles/mapbox-dash/standard-satellite-navigation"

        /**
         * Default 3D style.
         */
        const val DEFAULT_3D_STYLE = "mapbox://styles/mapbox-dash/standard-navigation"
    }
}
