package com.mapbox.dash.example

import android.app.Application
import android.location.LocationManager
import com.mapbox.common.location.Location
import com.mapbox.dash.cluster.dayStyleUri
import com.mapbox.dash.cluster.map3DStyleUri
import com.mapbox.dash.cluster.nightStyleUri
import com.mapbox.dash.driver.notification.presentation.DashIncidentType
import com.mapbox.dash.ev.api.EvDataProvider
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.base.device.DashDeviceType
import com.mapbox.dash.sdk.base.domain.model.BatteryComfortLevel
import com.mapbox.dash.sdk.base.units.Gb
import com.mapbox.dash.sdk.config.api.EngineType
import com.mapbox.dash.sdk.config.api.ScreenDirectionality
import com.mapbox.dash.sdk.config.dsl.DEFAULT_3D_STYLE
import com.mapbox.dash.sdk.config.dsl.DEFAULT_DAY_STYLE
import com.mapbox.dash.sdk.config.dsl.DEFAULT_NIGHT_STYLE
import com.mapbox.dash.sdk.config.dsl.borderCrossingNotification
import com.mapbox.dash.sdk.config.dsl.camera
import com.mapbox.dash.sdk.config.dsl.cluster
import com.mapbox.dash.sdk.config.dsl.debugSettings
import com.mapbox.dash.sdk.config.dsl.destinationPreview
import com.mapbox.dash.sdk.config.dsl.driverNotification
import com.mapbox.dash.sdk.config.dsl.ev
import com.mapbox.dash.sdk.config.dsl.fasterRouteNotification
import com.mapbox.dash.sdk.config.dsl.incidentNotification
import com.mapbox.dash.sdk.config.dsl.locationSimulation
import com.mapbox.dash.sdk.config.dsl.mapGpt
import com.mapbox.dash.sdk.config.dsl.mapStyle
import com.mapbox.dash.sdk.config.dsl.offline
import com.mapbox.dash.sdk.config.dsl.roadCameraNotification
import com.mapbox.dash.sdk.config.dsl.routeOptions
import com.mapbox.dash.sdk.config.dsl.search
import com.mapbox.dash.sdk.config.dsl.slowTrafficNotification
import com.mapbox.dash.sdk.config.dsl.speedLimitsOptions
import com.mapbox.dash.sdk.config.dsl.theme
import com.mapbox.dash.sdk.config.dsl.ui
import com.mapbox.dash.sdk.config.dsl.uiSettings
import com.mapbox.dash.state.defaults.camera.SimpleDefaults
import com.mapbox.maps.MapboxExperimental
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MainApplication : Application() {

    @OptIn(MapboxExperimental::class)
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
                nightStyleRes = R.style.MyDashTheme_Night
            }
            ev {
                autoAddChargingStationOnRoutePlanning = true
                batteryComfortLevel = BatteryComfortLevel.Range(kilometers = 80.0)
            }
            mapGpt {
                // Example of managing the visibility of user-accessible settings for MapGPT.
                isSettingsMenuEnabled = true
            }
            mapStyle {
                pixelRatioMultiplier = 1F
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
                freeDriveCamera3DModeEnabled = true
            }
            camera {
                freeDrive3DMode = SimpleDefaults(zoom = 16.5, pitch = 30.0)
                lookAheadMeters = 1.0
            }
            destinationPreview {
                skipDestinationPreview = false
            }

            // Setup small thresholds to be able to reproduce driver notifications easily
            driverNotification {
                fasterRouteNotification {
                    minFasterRouteDurationDiff = 1.minutes
                    fasterRouteNotificationFreeze = 0.minutes
                }
                borderCrossingNotification {
                    distanceToBorder = 600.0
                }
                slowTrafficNotification {
                    minSlowTrafficDelay = 10.seconds
                }
                roadCameraNotification {
                    distanceToCamera = 500.0
                    withSound = true
                }
                incidentNotification {
                    distanceToIncident = 600.0
                    timeToDismiss = 15.seconds
                    enabledIncidentTypes = setOf(
                        DashIncidentType.ACCIDENT,
                        DashIncidentType.CONSTRUCTION,
                        DashIncidentType.DISABLED_VEHICLE,
                        DashIncidentType.LANE_RESTRICTION,
                        DashIncidentType.MASS_TRANSIT,
                        DashIncidentType.MISCELLANEOUS,
                        DashIncidentType.OTHER_NEWS,
                        DashIncidentType.PLANNED_EVENT,
                        DashIncidentType.ROAD_CLOSURE,
                        DashIncidentType.ROAD_HAZARD,
                        DashIncidentType.WEATHER,
                        DashIncidentType.UNKNOWN,
                    )
                    withSound = true
                }
            }
            cluster {
                enabled = true
                dayStyleUri = DEFAULT_DAY_STYLE
                nightStyleUri = DEFAULT_NIGHT_STYLE
                map3DStyleUri = DEFAULT_3D_STYLE
            }

            engineType = EngineType.ELECTRIC
            device = DashDeviceType.Automobile
        }
        configureEvProvider()
    }

    /**
     * Configure [EvDataProvider] for Electric Vehicle Data
     *
     * Reference Docs https://docs.mapbox.com/android/navigation/ux/guides/coordination/electric-vehicle/
     * Interface Docs https://docs.mapbox.com/android/navigation/api/uxframework/1.0.0-beta.40/ev-api/com.mapbox.dash.ev.api/-ev-data-provider/?query=interface%20EvDataProvider
     */
    private fun configureEvProvider() {
        val evDataProvider = object : EvDataProvider {
            override val stateOfCharge = flowOf(50f)
            override val minBatteryRange = flowOf<Float?>(200f)
            override val maxBatteryRange = flowOf<Float?>(240f)
            override val secondsRemainingToCharge = flowOf<Int?>(7200)
            override val isChargerPluggedIn = flowOf(true)
            override val maxCharge = flowOf(57500)
            override val connectorTypes = flowOf("ccs_combo_type1")
            override val energyConsumptionCurve = flowOf("0,300;20,160;80,140;120,180")
            override val chargingCurve = flowOf("0,100000;40000,70000;60000,30000;80000,10000")
        }

        Dash.controller.setEvDataProvider(evDataProvider)
    }

    internal companion object {

        val MAPBOX_DC_OFFICE: Location = Location.Builder()
            .source(LocationManager.PASSIVE_PROVIDER)
            .latitude(38.899929)
            .longitude(-77.03394)
            .build()
    }
}
