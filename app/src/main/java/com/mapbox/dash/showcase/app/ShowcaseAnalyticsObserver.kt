package com.mapbox.dash.showcase.app

import android.util.Log
import com.mapbox.dash.sdk.analytics.DashAnalyticsEvent
import com.mapbox.dash.sdk.analytics.DashAnalyticsObserver

private const val TAG = "ShowcaseAnalyticsObserver"

class ShowcaseAnalyticsObserver : DashAnalyticsObserver {

    override fun onDashAnalyticsEvent(event: DashAnalyticsEvent) {
        Log.d(TAG, "onDashAnalyticsEvent: $event")
    }
}
