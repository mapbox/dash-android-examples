package com.mapbox.dash.showcase.app

import android.app.Application
import android.location.LocationManager
import com.mapbox.annotation.MapboxExperimental
import com.mapbox.common.location.Location
import com.mapbox.dash.cluster.cluster
import com.mapbox.dash.cluster.mapStyle
import com.mapbox.dash.logging.LogsExtra
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.base.domain.model.AutoAddChargingStrategy
import com.mapbox.dash.sdk.base.domain.model.BatteryComfortLevel
import com.mapbox.dash.sdk.base.units.Gb
import com.mapbox.dash.sdk.config.api.DEFAULT_DAY_STYLE
import com.mapbox.dash.sdk.config.api.DEFAULT_NIGHT_STYLE
import com.mapbox.dash.sdk.config.api.DEFAULT_SATELLITE_STYLE
import com.mapbox.dash.sdk.config.api.DashFasterRouteNotificationAcceptanceStrategy
import com.mapbox.dash.sdk.config.api.DashFasterRouteNotificationTimeoutAction
import com.mapbox.dash.sdk.config.api.DashIncidentNotificationType
import com.mapbox.dash.sdk.config.api.MapIncidentsVisibility
import com.mapbox.dash.sdk.config.api.MapTrafficLightsVisibility
import com.mapbox.dash.sdk.config.api.MapTrafficVisibility
import com.mapbox.dash.sdk.config.api.OfflineSearchDataset
import com.mapbox.dash.sdk.config.api.OfflineTileLanguage
import com.mapbox.dash.sdk.config.api.SearchEntranceMode
import com.mapbox.dash.sdk.config.api.StreetNameVisibility
import com.mapbox.dash.sdk.config.api.UiModeSettings
import com.mapbox.dash.sdk.config.api.UnitOfMeasurement
import com.mapbox.dash.sdk.config.api.WorldviewCountryCode
import com.mapbox.dash.sdk.config.api.analytics
import com.mapbox.dash.sdk.config.api.borderCrossingNotification
import com.mapbox.dash.sdk.config.api.camera
import com.mapbox.dash.sdk.config.api.debugSettings
import com.mapbox.dash.sdk.config.api.destinationPreview
import com.mapbox.dash.sdk.config.api.driverNotification
import com.mapbox.dash.sdk.config.api.ev
import com.mapbox.dash.sdk.config.api.evTripNotification
import com.mapbox.dash.sdk.config.api.fasterRouteNotification
import com.mapbox.dash.sdk.config.api.incidentNotification
import com.mapbox.dash.sdk.config.api.locationSimulation
import com.mapbox.dash.sdk.config.api.mapStyle
import com.mapbox.dash.sdk.config.api.network
import com.mapbox.dash.sdk.config.api.offline
import com.mapbox.dash.sdk.config.api.roadCameraNotification
import com.mapbox.dash.sdk.config.api.routeOptions
import com.mapbox.dash.sdk.config.api.search
import com.mapbox.dash.sdk.config.api.slowTrafficNotification
import com.mapbox.dash.sdk.config.api.speedLimitsOptions
import com.mapbox.dash.sdk.config.api.theme
import com.mapbox.dash.sdk.config.api.ui
import com.mapbox.dash.sdk.config.api.uiSettings
import com.mapbox.dash.sdk.config.api.voices
import com.mapbox.dash.sdk.ev.api.EvDataProvider
import com.mapbox.dash.sdk.ev.domain.model.ChargingCurve
import com.mapbox.dash.sdk.ev.domain.model.ChargingPoint
import com.mapbox.dash.sdk.ev.domain.model.Energy
import com.mapbox.dash.sdk.ev.domain.model.Energy.Companion.kiloWattHours
import com.mapbox.dash.sdk.navigation.coordination.config.NavigationCoordinationMode
import com.mapbox.dash.sdk.navigation.coordination.config.navCoordination
import com.mapbox.dash.sdk.signals.api.domain.model.VehicleEvConnectorType
import com.mapbox.dash.showcase.app.theme.CustomThemeFactory
import com.mapbox.dash.state.defaults.camera.ActiveGuidanceDefaults
import com.mapbox.dash.state.defaults.camera.FollowingDefaults
import com.mapbox.navigation.audio.text.PersonaVoice
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.mapgpt.mapGpt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ShowcaseApp : Application() {

    @OptIn(
        ExperimentalPreviewMapboxNavigationAPI::class,
        MapboxExperimental::class,
        com.mapbox.maps.MapboxExperimental::class,
    )
    @Suppress("MagicNumber")
    override fun onCreate() {
        super.onCreate()
        Dash.init(
            context = this,
            accessToken = getString(R.string.mapbox_access_token),
        ) {
            logLevel = LogsExtra.LOG_LEVEL_DEBUG
            unitOfMeasurement = UnitOfMeasurement.AUTO
            theme {
                themeFactory = CustomThemeFactory
            }

            mapStyle {
                dayStyleUri = DAY_MAP_STYLE
                nightStyleUri = NIGHT_MAP_STYLE
                satelliteStyleUri = SATELLITE_MAP_STYLE
                worldviewCountryCode = WorldviewCountryCode.MOROCCO
                // Apply 3d live style if HDLite is enabled in navCoordination config
                // map3dStyleUri = "mapbox://styles/mapbox-3dln/hd-roads-3dln-style"
            }

            navCoordination {
                navCoordinationMode = NavigationCoordinationMode.SD
            }

            voices {
                selectedVoice = PersonaVoice.voice1
            }
            mapGpt {
                isEnabled = BuildConfig.MAP_GPT_ENABLED
                isSettingsMenuEnabled = true
                profileId = "default"
            }
            locationSimulation {
                defaultLocation = MAPBOX_DC_OFFICE
            }
            routeOptions {
                avoidHighways = true
                avoidFerries = true
                avoidTolls = true
                avoidUnpavedRoads = true
                includeHov2 = true
                includeHov3 = true
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
                querySearchLimit = 5
                categorySearchLimit = 25
                resultsAdapter = ShowcaseSearchResultsAdapter
                searchEngine = ShowcaseSearchEngine
                entranceMode = SearchEntranceMode.SEARCH
            }
            camera {
                lookAheadMeters = 1.0
                freeDriveDefaults = FollowingDefaults(zoom = 15.0)
                activeGuidanceDefaults = ActiveGuidanceDefaults(defaultPitch = 45.0)
                isShoveGestureEnabled = false
            }
            ui {
                uiModeSettings = UiModeSettings.AUTO
                streetNameVisibility = StreetNameVisibility.ONLY_FREE_DRIVE
                mapIncidentsVisibility = MapIncidentsVisibility.ONLY_FREE_DRIVE
                mapTrafficLightsVisibility = MapTrafficLightsVisibility.ALWAYS
                mapTrafficVisibility = MapTrafficVisibility.NEVER
                showOfflineModeInfo = false
            }

            network {
                isMapboxStackConnected = true
            }

            offline {
                tilesPath = applicationContext.filesDir.absolutePath + "/prepared_tilestore"
                tilesDiskQuota = 27.Gb
                dataset = OfflineSearchDataset(
                    name = "mbx-gen2-ev",
                    language = OfflineTileLanguage.EN,
                )
            }

            debugSettings {
                showSimulateLocationOption = true
                showDisplaySendDebugInfoButtonOption = true
                showSendDebugInfoButton = true
                showElectricOptionsItem = true
                showUseAddressForSnapping = true
            }

            destinationPreview {
                skipDestinationPreview = true
            }

            analytics {
                analyticsObserver = ShowcaseAnalyticsObserver()
            }

            driverNotification {

                // Setup small values to trigger faster route notification for debug
                fasterRouteNotification {
                    fasterRouteNotificationFreeze = 1.minutes
                    minFasterRouteDurationDiff = 1.minutes
                    acceptanceStrategy = DashFasterRouteNotificationAcceptanceStrategy.SWITCH_TO_FASTER_ROUTE
                    timeoutAction = DashFasterRouteNotificationTimeoutAction.AUTO_ACCEPT
                }

                borderCrossingNotification {
                    distanceToBorder = 600.0
                }

                slowTrafficNotification {
                    minSlowTrafficDelay = 1.minutes
                }

                roadCameraNotification {
                    distanceToCamera = 1000.0
                    withSound = true
                }

                incidentNotification {
                    distanceToIncident = 600.0
                    timeToDismiss = 15.seconds
                    enabledIncidentTypes = setOf(
                        DashIncidentNotificationType.Accident,
                        DashIncidentNotificationType.Congestion,
                        DashIncidentNotificationType.Construction,
                        DashIncidentNotificationType.DisabledVehicle,
                        DashIncidentNotificationType.LaneRestriction,
                        DashIncidentNotificationType.MassTransit,
                        DashIncidentNotificationType.Miscellaneous,
                        DashIncidentNotificationType.OtherNews,
                        DashIncidentNotificationType.PlannedEvent,
                        DashIncidentNotificationType.RoadClosure,
                        DashIncidentNotificationType.RoadHazard,
                        DashIncidentNotificationType.Weather,
                        DashIncidentNotificationType.Unknown,
                    )
                    withSound = true
                }

                evTripNotification {
                    evBetterRouteNotificationFreeze = 1.minutes
                    minSocChangeThreshold = 1
                    minChargeTimeChangeThreshold = 3.minutes
                }
            }

            ev {
                autoAddChargingStations = AutoAddChargingStrategy.Always
                batteryComfortLevel = BatteryComfortLevel.Range(kilometers = 80.0)
            }

            cluster {
                mapStyle {
                    dayStyleUri = DAY_MAP_STYLE
                    nightStyleUri = NIGHT_MAP_STYLE
                    satelliteStyleUri = SATELLITE_MAP_STYLE
                }
            }
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
            override val secondsRemainingToCharge = flowOf<Int?>(7200)
            override val stateOfEnergy: Flow<Energy> = flowOf(60.kiloWattHours)
            override val isCharging = flowOf(true)
            override val maxCharge: Flow<Energy> = flowOf(100.kiloWattHours)
            override val connectorTypes = flowOf(setOf(VehicleEvConnectorType.IEC_TYPE_1_CCS_DC))
            override val efficiency = flowOf(5f) // 5 kilometers per kWh
            override val energyConsumptionCurveReal = flowOf("0,300;20,160;80,140;120,180")
            override val energyConsumptionCurveFreeflow = flowOf("0,300;20,160;80,140;120,180")
            override val energyConsumptionCurveBlendingRatio = flowOf<String?>(null)
            override val energyConsumptionCurveSpeedLimit = flowOf<Int?>(null)
            override val chargingCurve = flowOf(
                ChargingCurve(
                    listOf(
                        ChargingPoint(0.kiloWattHours, 100_000),
                        ChargingPoint(40.kiloWattHours, 70_000),
                        ChargingPoint(60.kiloWattHours, 30_000),
                        ChargingPoint(80.kiloWattHours, 10_000),
                    )
                ),
            )
        }

        Dash.controller.setEvDataProvider(evDataProvider)
    }

    internal companion object {

        const val DAY_MAP_STYLE = DEFAULT_DAY_STYLE // "mapbox://styles/mapbox-map-design/clfckc6nb001701o58l0mykxu"
        const val NIGHT_MAP_STYLE = DEFAULT_NIGHT_STYLE // "mapbox://styles/mapbox-map-design/clfckvxvo000y01rgd2uosani"
        const val SATELLITE_MAP_STYLE =
            DEFAULT_SATELLITE_STYLE // "mapbox://styles/mapbox-map-design/ckv0ri8h322nt14qkpdi1g60m"

        private val MAPBOX_DC_OFFICE = Location.Builder()
            .source(LocationManager.PASSIVE_PROVIDER)
            .latitude(38.899929)
            .longitude(-77.03394)
            .build()
    }
}
