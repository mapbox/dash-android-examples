package com.mapbox.dash.example

import android.app.Application
import android.location.LocationManager
import com.mapbox.common.location.Location
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.base.device.DashDeviceType
import com.mapbox.dash.sdk.base.units.Gb
import com.mapbox.dash.sdk.config.api.CustomKeys
import com.mapbox.dash.sdk.config.api.EngineType
import com.mapbox.dash.sdk.config.api.ScreenDirectionality
import com.mapbox.dash.sdk.config.dsl.debugSettings
import com.mapbox.dash.sdk.config.dsl.locationSimulation
import com.mapbox.dash.sdk.config.dsl.mapGpt
import com.mapbox.dash.sdk.config.dsl.mapStyle
import com.mapbox.dash.sdk.config.dsl.offline
import com.mapbox.dash.sdk.config.dsl.routeOptions
import com.mapbox.dash.sdk.config.dsl.search
import com.mapbox.dash.sdk.config.dsl.speedLimitsOptions
import com.mapbox.dash.sdk.config.dsl.theme
import com.mapbox.dash.sdk.config.dsl.ui
import com.mapbox.dash.sdk.config.dsl.uiSettings

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start by initializing the Dash SDK by providing a configuration instance.
        // You can read more about configuration in https://docs.mapbox.com/android/navigation/build-with-uxframework/configuration/.
        Dash.init(
            context = applicationContext,
            accessToken = getString(R.string.mapbox_access_token),
        ) {
            theme {
                // Example of providing a custom theme to change the look-and-feel of Dash's UI components.
                dayStyleRes = R.style.MyDashTheme_Day
            }
            mapGpt {
                // Example of managing the visibility of user-accessible settings for MapGPT.
                isSettingsMenuEnabled = true
            }
            mapStyle {
                pixelRatio = applicationContext.resources.displayMetrics.density
            }
            locationSimulation {
                // Example of a debug option that teleports the user to a specific location at each app launch.
                defaultLocation = MAPBOX_DC_OFFICE
            }
            routeOptions {
                avoidHighways = false
                avoidFerries = false
                avoidTolls = false
            }
            speedLimitsOptions {
                showSpeedLimits = true
                showSpeedWarnings = true
            }
            uiSettings {
                showRouteOptions = true
                showSpeedLimitsOptions = true
            }
            search {
                resultsAdapter = ShowcaseSearchResultsAdapter()
                searchEngine = ShowcaseSearchEngine()
            }
            offline {
                tilesPath = applicationContext.filesDir.absolutePath + "/prepared_tilestore"
                tilesDiskQuota = 30.Gb
            }
            debugSettings {
                showSendDebugInfoButton = true
                showDisplaySendDebugInfoButtonOption = true
            }
            ui {
                screenDirectionality = ScreenDirectionality.LEFT_TO_RIGHT
            }
            engineType = EngineType.ELECTRIC
            device = DashDeviceType.Automobile

            customValues[CustomKeys.SINGLE_CHARGER_MANUAL_SELECTION] = true
            customValues[CustomKeys.ENABLE_QUICK_SEARCH_SUGGESTIONS_IN_ACTIVE_GUIDANCE] = false
            customValues[CustomKeys.FORCE_DEBUG] = true
        }
    }

    internal companion object {

        val MAPBOX_DC_OFFICE = Location.Builder()
            .source(LocationManager.PASSIVE_PROVIDER)
            .latitude(38.899929)
            .longitude(-77.03394)
            .build()
    }
}
