package com.mapbox.dash.example.service

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

internal class UXFBackgroundServiceLauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceIntent = Intent(this, UXFBackgroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
        finish()
    }
}
