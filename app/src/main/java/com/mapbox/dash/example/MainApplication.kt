package com.mapbox.dash.example

import android.app.Application
import android.location.Location
import android.location.LocationManager
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.config.api.DashConfig

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
                dayStyleRes = R.style.DayTheme
            }
            mapGptConfig {
                // Example of managing the visibility of user-accessible settings for MapGPT.
                isSettingsMenuEnabled = true
            }
            locationSimulation {
                // Example of a debug option that teleports the user to a specific location at each app launch.
                defaultLocation = MAPBOX_DC_OFFICE
            }
        }

        // Finally, initialize Dash SDK which loads all necessary runtime modules.
        Dash.init(config)
    }

    internal companion object {
        val MAPBOX_DC_OFFICE = Location(LocationManager.PASSIVE_PROVIDER).apply {
            latitude = 38.899929
            longitude = -77.03394
        }
    }
}