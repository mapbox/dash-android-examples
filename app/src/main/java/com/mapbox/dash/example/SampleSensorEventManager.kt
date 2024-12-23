package com.mapbox.dash.example

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.dash.sdk.data.inputs.api.model.DashCompassData
import com.mapbox.navigation.base.geometry.Angle.Companion.degrees
import com.mapbox.navigation.base.geometry.Point3D
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged

internal class SampleSensorEventManager(private val context: Context) : SensorEventListener {

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    fun onResume() {
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (accelerometer == null) {
            Log.d(TAG, "Accelerometer is not available")
        } else if (magnetometer == null) {
            Log.d(TAG, "Magnetometer is not available")
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun onPause() {
        sensorManager.unregisterListener(this)
    }

    private val _compassData = MutableSharedFlow<DashCompassData>(extraBufferCapacity = 1)
    val compassData = _compassData.distinctUntilChanged()

    private var currentCompassData: DashCompassData? = null

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null
    private var azimuth = 0f

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values
        }

        val latestGravity = gravity
        val latestGeomagnetic = geomagnetic
        if (latestGravity != null && latestGeomagnetic != null) {
            val rotationMatrix = FloatArray(9)
            val inclinationMatrix = FloatArray(9)

            val rotationMatrixSuccess = SensorManager.getRotationMatrix(
                rotationMatrix,
                inclinationMatrix,
                latestGravity,
                latestGeomagnetic,
            )

            if (rotationMatrixSuccess) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)
                azimuth = Math.toDegrees(orientation[0].toDouble()).normalizeDegrees().toFloat()

                calculateTrueNorth(azimuth) { trueNorth ->
                    val rawGeomagneticData = Point3D(
                        x = latestGravity[0].toDouble(),
                        y = latestGravity[1].toDouble(),
                        z = latestGravity[2].toDouble(),
                    )

                    val newCompassData = DashCompassData(
                        magneticHeading = azimuth.degrees,
                        trueHeading = trueNorth?.degrees,
                        headingAccuracy = null,
                        rawGeomagneticData = rawGeomagneticData,
                        monotonicTimestampNanoseconds = System.nanoTime(),
                    )
                    _compassData.tryEmit(newCompassData)
                    currentCompassData = newCompassData
                }
            }
        }
    }

    override fun onAccuracyChanged(sens: Sensor, accuracy: Int) {
        /**
         * Should we map accuracy level (SENSOR_STATUS_*) to some predefined absolute value
         * we can us in [CompassData.headingAccuracy]?
         */
    }

    private fun calculateTrueNorth(magneticNorth: Float, resultCallback: (Float?) -> Unit) {
        LocationServiceFactory.getOrCreate().getDeviceLocationProvider(null)
            .onValue { provider ->
                provider.getLastLocation { location ->
                    val altitude = location?.altitude?.toFloat()
                    if (location != null && altitude != null) {
                        val geomagneticField = GeomagneticField(
                            location.latitude.toFloat(),
                            location.longitude.toFloat(),
                            altitude,
                            location.timestamp,
                        )
                        resultCallback((magneticNorth + geomagneticField.declination).normalizeDegrees())
                    } else {
                        resultCallback(null)
                    }
                }
            }.onError {
                resultCallback(null)
            }
    }

    private fun Double.normalizeDegrees() = ((this % 360.0) + 360.0) % 360.0

    private fun Float.normalizeDegrees() = ((this % 360F) + 360F) % 360F

    private companion object {
        const val TAG = "SensorEventManager"
    }
}
