package com.mapbox.dash.example

import android.app.Application
import android.location.LocationManager
import com.mapbox.common.location.Location
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.config.api.DashConfig
import com.mapbox.dash.sdk.config.api.EngineType

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start by initializing the Dash SDK by providing a configuration instance.
        // You can read more about configuration in https://docs.mapbox.com/android/dash/guides/configuration/.
        val config = DashConfig.create(
            applicationContext = applicationContext,
            accessToken = getString(R.string.mapbox_access_token),
        ) {
            themeConfig {
                // Example of providing a custom theme to change the look-and-feel of Dash's UI components.
                dayStyleRes = R.style.MyDashTheme_Day
            }
            mapGptConfig {
                // Example of managing the visibility of user-accessible settings for MapGPT.
                isSettingsMenuEnabled = true
            }
            mapStyleConfig {
                pixelRatio = applicationContext.resources.displayMetrics.density
            }
            locationSimulation {
                // Example of a debug option that teleports the user to a specific location at each app launch.
                defaultLocation = MAPBOX_DC_OFFICE
            }
            routeOptionsConfig {
                avoidHighways = true
                avoidFerries = true
                avoidTolls = true
            }
            speedLimitsOptionsConfig {
                showSpeedLimits = true
                showSpeedWarnings = true
            }
            uiSettingsConfig {
                showRouteOptions = true
                showSpeedLimitsOptions = true
                useSidebar = true
            }
            searchConfig {
                resultsAdapter = ShowcaseSearchResultsAdapter()
            }
            engineType = EngineType.ELECTRIC
        }

        // Finally, initialize Dash SDK which loads all necessary runtime modules.
        Dash.init(config)
    }

    internal companion object {
        val MAPBOX_DC_OFFICE = Location.Builder()
            .source(LocationManager.PASSIVE_PROVIDER)
            .latitude(38.899929)
            .longitude(-77.03394)
            .build()
    }
}
