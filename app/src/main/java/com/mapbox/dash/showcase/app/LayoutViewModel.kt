package com.mapbox.dash.showcase.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LayoutViewModel : ViewModel() {

    val headlessMode = MutableStateFlow(BuildConfig.HEADLESS_MODE_ENABLED)
    val navigationSuggestionsEnabled = MutableStateFlow(false)
    val customSearchResults = MutableStateFlow(false)
    val reverseUiMode = MutableStateFlow(false)
    val setCustomMarkerFactory = MutableStateFlow(value = false)
    val setCustomPlacesListComposer = MutableStateFlow(value = false)
    val setCustomPlacePreviewComposer = MutableStateFlow(value = false)
    val setCustomManeuver = MutableStateFlow(value = false)
    val setCustomUpcomingManeuvers = MutableStateFlow(value = false)
    val setCustomSearchScreen = MutableStateFlow(value = false)
    val setCustomOfflineRouteAlert = MutableStateFlow(value = false)
    val setCustomResumeGuidanceView = MutableStateFlow(value = false)
    val setCustomCompassDataInput = MutableStateFlow(value = false)
    val setCustomRoutesOverviewComposer = MutableStateFlow(value = false)
    val showRouteOptionsInSettings = MutableStateFlow(value = true)
    val showSpeedLimitsOptionsInSettings = MutableStateFlow(value = true)
    val overrideSidebarControls = MutableStateFlow(value = false)
    val searchPanelPosition = MutableStateFlow(SearchPanelPosition.BottomLeft.name)
    val overrideSearchPanelButtons = MutableStateFlow(value = false)
    val simpleCardHeader = MutableStateFlow(value = false)
    val setCustomStreetName = MutableStateFlow(value = false)
    val upcomingLaneGuidance = MutableStateFlow(value = false)
    val avoidHighways = MutableStateFlow(value = true)
    val enableMapLayer = MutableStateFlow(value = false)
    val enableWeatherAlongRoute = MutableStateFlow(value = false)
    val avoidTolls = MutableStateFlow(value = true)
    val avoidFerries = MutableStateFlow(value = true)
    val setCustomTripSummary = MutableStateFlow(value = false)
    val setCustomEndActiveGuidance = MutableStateFlow(value = false)
    val setCustomEditTrip = MutableStateFlow(value = false)
    val setCustomContinueNavigation = MutableStateFlow(value = false)
    val setCustomArrivalFeedback = MutableStateFlow(value = false)
    val leftHandTrafficLayout = MutableStateFlow(value = false)
    val setCustomDriverNotification = MutableStateFlow(value = false)
    val overrideRecenterPill = MutableStateFlow(value = false)
    val overrideSearchThisArea = MutableStateFlow(value = false)
    val setCustomRangeMapInfoView = MutableStateFlow(value = false)
    val enableEvChargePoint = MutableStateFlow(value = false)
    val shoveGestureEnabled = MutableStateFlow(value = false)

    init {
        viewModelScope.launch {
            customSearchResults.collect {
                ShowcaseSearchResultsAdapter.enabled = it
            }
        }
    }
}
