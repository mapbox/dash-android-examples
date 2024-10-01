package com.mapbox.dash.example.service

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mapbox.dash.example.R
import com.mapbox.dash.example.service.UXFBackgroundService.Companion.FOREGROUND_NOTIFICATION_CHANNEL_ID

internal class UXFBackgroundServiceLauncherActivity : AppCompatActivity() {

    // variable uses to prevent false-positive call checkNotificationEnabled on resume, cause
    // permission launcher emmit run new activity and calls onResume BEFORE activity result
    private var permissionRequested = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { _ ->
        permissionRequested = false
        checkNotificationEnabled()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val state = getNotificationPermissionState()
        when (state) {
            PermissionState.GRANTED -> checkNotificationEnabled()

            PermissionState.NOT_GRANTED -> {
                permissionRequested = true
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            PermissionState.SHOULD_SHOW_RATIONALE -> {
                // Do nothing, will be called on resume
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!permissionRequested) {
            checkNotificationEnabled()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannelCompat
            .Builder(
                FOREGROUND_NOTIFICATION_CHANNEL_ID,
                NotificationManagerCompat.IMPORTANCE_DEFAULT,
            )
            .setName(this.getString(R.string.shortcut_service_label))
            .build()
        NotificationManagerCompat.from(this)
            .createNotificationChannel(channel)
    }

    @Suppress("LongMethod")
    private fun checkNotificationEnabled() {
        when {
            areNotificationsEnabled() && isLocationPermissionGranted() &&
                    isNotificationChannelEnabled() -> {
                startServiceAndFinishActivity()
            }

            !areNotificationsEnabled() -> {
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_destination)
                    .setTitle("Notification permission")
                    .setMessage(
                        "To start work with foreground service you should " +
                                "grant permission to show notification",
                    )
                    .setPositiveButton("Grant permission") { _, _ ->
                        val settingsIntent: Intent =
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        startActivity(settingsIntent)
                    }
                    .setNegativeButton("Cancel") { _, _ -> finish() }
                    .show()
            }

            !isLocationPermissionGranted() -> {
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_destination)
                    .setTitle("Location permission")
                    .setMessage(
                        "To start work with foreground service you should " +
                                "grant location permissions",
                    )
                    .setPositiveButton("Grant permission") { _, _ ->
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null),
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { _, _ -> finish() }
                    .show()
            }

            else -> {
                AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_destination)
                    .setTitle("Notification permission")
                    .setMessage(
                        "To start work with foreground service you should grant" +
                                " permission to show notification " +
                                "channel: ${getString(R.string.shortcut_service_label)}",
                    )
                    .setPositiveButton("Grant permission") { _, _ ->
                        val settingsIntent: Intent =
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                                .putExtra(
                                    Settings.EXTRA_CHANNEL_ID,
                                    FOREGROUND_NOTIFICATION_CHANNEL_ID,
                                )
                        startActivity(settingsIntent)
                    }
                    .setNegativeButton("Cancel") { _, _ -> finish() }
                    .show()
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        val locationPermissions =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.FOREGROUND_SERVICE_LOCATION,
                )
            } else {
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            }
        return locationPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun areNotificationsEnabled(): Boolean =
        NotificationManagerCompat.from(this).areNotificationsEnabled()

    private fun isNotificationChannelEnabled(): Boolean {
        val notificationManager = NotificationManagerCompat.from(this)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager
                .getNotificationChannelCompat(FOREGROUND_NOTIFICATION_CHANNEL_ID)
            channel != null && channel.importance != NotificationManager.IMPORTANCE_NONE
        } else {
            true
        }
    }

    private fun startServiceAndFinishActivity() {
        val serviceIntent = Intent(this, UXFBackgroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        finish()
    }

    private fun getNotificationPermissionState(): PermissionState = if (
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    ) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED -> PermissionState.GRANTED

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->
                PermissionState.SHOULD_SHOW_RATIONALE

            else -> PermissionState.NOT_GRANTED
        }
    } else {
        PermissionState.SHOULD_SHOW_RATIONALE
    }

    enum class PermissionState {
        GRANTED,
        NOT_GRANTED,
        SHOULD_SHOW_RATIONALE,
    }
}
