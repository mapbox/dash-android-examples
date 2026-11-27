package com.mapbox.dash.showcase.app

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener

@Suppress("EmptyFunctionBlock")
internal class SampleSensorEventManager(
    val accelerometer: Sensor,
    val magnetometer: Sensor,
) : SensorEventListener {

    fun collectCompassData() {
    }

    override fun onSensorChanged(event: SensorEvent) {
    }

    override fun onAccuracyChanged(sens: Sensor, accuracy: Int) {
    }
}
