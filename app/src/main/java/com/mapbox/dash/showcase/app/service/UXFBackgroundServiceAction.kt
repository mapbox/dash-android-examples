package com.mapbox.dash.showcase.app.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mapbox.dash.showcase.app.R

internal enum class UXFBackgroundServiceAction(
    private val title: String,
    private val action: String,
) {
    START_NAVIGATION("Start navigation", "start_navigation"),
    STOP_NAVIGATION("Stop navigation", "stop_navigation"),
    ;

    fun createAction(context: Context): NotificationCompat.Action {
        val intent = Intent(context, UXFBackgroundService::class.java)
            .setAction(this.action)
        val pendingIntent = PendingIntent
            .getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action.Builder(
            com.mapbox.navigation.base.R.drawable.mapbox_ic_navigation,
            title,
            pendingIntent,
        ).build()
    }

    companion object {
        fun fromAction(action: String): UXFBackgroundServiceAction? =
            entries.firstOrNull { it.action == action }
    }
}
