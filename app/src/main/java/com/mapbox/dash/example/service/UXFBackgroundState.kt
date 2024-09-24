package com.mapbox.dash.example.service

internal enum class UXFBackgroundState(
    val title: String,
    val action: UXFBackgroundServiceAction,
) {
    ACTIVE_GUIDANCE(
        title = "Active guidance",
        action = UXFBackgroundServiceAction.STOP_NAVIGATION,
    ),
    FREE_DRIVE(
        title = "Free drive",
        action = UXFBackgroundServiceAction.SET_DESTINATION,
    ),
    TRIP_PLANNING(
        title = "Trip planning",
        action = UXFBackgroundServiceAction.START_NAVIGATION,
    ),
}
