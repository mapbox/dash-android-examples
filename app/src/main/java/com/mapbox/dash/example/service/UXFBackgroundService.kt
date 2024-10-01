package com.mapbox.dash.example.service

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.mapbox.dash.example.R
import com.mapbox.dash.example.getRandomDestinationAround
import com.mapbox.dash.logging.extension.className
import com.mapbox.dash.sdk.Dash
import com.mapbox.dash.sdk.event.NavigationState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class UXFBackgroundService : Service() {

    private val coroutineScope: CoroutineScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        ServiceCompat.startForeground(
            this,
            FOREGROUND_SERVICE_ID,
            buildNotification(state = UXFBackgroundState.FREE_DRIVE),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                0
            },
        )
        Dash.controller.startTripSession().onFailure {
            Toast.makeText(this, "Failed to start trip session", Toast.LENGTH_SHORT).show()
        }
        Dash.controller.observeNavigationState()
            .distinctUntilChangedBy { it.className }
            .onEach {
                val state = when (it) {
                    is NavigationState.ActiveGuidance -> UXFBackgroundState.ACTIVE_GUIDANCE
                    is NavigationState.TripPlanning -> UXFBackgroundState.TRIP_PLANNING
                    else -> UXFBackgroundState.FREE_DRIVE
                }
                updateNotification(state)
            }
            .launchIn(coroutineScope)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val controller = Dash.controller
        when (UXFBackgroundServiceAction.fromAction(intent?.action.orEmpty())) {
            UXFBackgroundServiceAction.SET_DESTINATION -> coroutineScope.launch {
                val location = controller.observeRawLocation().first()
                val destination = location.getRandomDestinationAround()
                controller.setDestination(destination)
            }

            UXFBackgroundServiceAction.START_NAVIGATION -> coroutineScope.launch {
                controller.startNavigation(controller.observeRoutes().first().routes.first())
            }

            UXFBackgroundServiceAction.STOP_NAVIGATION -> coroutineScope.launch {
                controller.stopNavigation()
            }

            null -> {
                // Do nothing
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Dash.controller.stopTripSession()
        coroutineScope.cancel()
    }

    private fun updateNotification(state: UXFBackgroundState = UXFBackgroundState.FREE_DRIVE) {
        val notification = buildNotification(state)
        updateNotification(notification)
    }

    private fun updateNotification(notification: Notification) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(FOREGROUND_SERVICE_ID, notification)
    }

    private fun buildNotification(
        state: UXFBackgroundState,
    ): Notification {
        return NotificationCompat.Builder(this, FOREGROUND_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Navigation state:")
            .setContentText(state.title)
            .setSmallIcon(R.drawable.ic_destination)
            .addAction(state.action.createAction(this))
            .setSound(null)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    internal companion object {
        const val FOREGROUND_NOTIFICATION_CHANNEL_ID = "general_notification_channel"
        private const val FOREGROUND_SERVICE_ID = 9765
    }
}
