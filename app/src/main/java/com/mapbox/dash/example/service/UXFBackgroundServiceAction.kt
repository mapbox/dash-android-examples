package com.mapbox.dash.example.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.mapbox.dash.example.R

internal enum class UXFBackgroundServiceAction(
    private val title: String,
    private val action: String,
) {
    SET_DESTINATION("Set destination", "set_destination"),
    START_NAVIGATION("Start navigation", "start_navigation"),
    STOP_NAVIGATION("Stop navigation", "stop_navigation"),
    ;

    fun createAction(context: Context): NotificationCompat.Action {
        val intent = Intent(context, UXFBackgroundService::class.java)
            .setAction(this.action)
        val pendingIntent = PendingIntent
            .getService(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action.Builder(
            R.drawable.ic_destination,
            title,
            pendingIntent,
        ).build()
    }

    companion object {
        fun fromAction(action: String): UXFBackgroundServiceAction? =
            values().firstOrNull { it.action == action }
    }
}
