package ilapin.common.android.magneticfield

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ilapin.common.android.log.L
import ilapin.common.magneticfield.MagneticField
import ilapin.common.magneticfield.MagneticFieldRepository
import io.reactivex.Observable

class SensorMagneticFieldRepository(private val sensorManager: SensorManager) : MagneticFieldRepository {

    override fun magneticField(): Observable<MagneticField> {
        return Observable.create { emitter ->
            val magneticFieldSensorListener = object : SensorEventListener {

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // do nothing
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(
                            MagneticField(
                                event.values[0],
                                event.values[1],
                                event.values[2],
                                when (event.accuracy) {
                                    SensorManager.SENSOR_STATUS_ACCURACY_LOW -> MagneticField.Accuracy.LOW
                                    SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> MagneticField.Accuracy.MEDIUM
                                    SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> MagneticField.Accuracy.HIGH
                                    else -> {
                                        L.e(LOG_TAG, "Unexpected magnetic field accuracy ${event.accuracy}")
                                        MagneticField.Accuracy.HIGH
                                    }
                                },
                                event.timestamp
                            )
                        )
                    }
                }
            }

            emitter.setCancellable {
                sensorManager.unregisterListener(magneticFieldSensorListener)
            }

            val magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            if (magneticFieldSensor != null) {
                sensorManager.registerListener(
                    magneticFieldSensorListener,
                    magneticFieldSensor,
                    SensorManager.SENSOR_DELAY_UI
                )
            } else {
                if (!emitter.isDisposed) {
                    emitter.onError(IllegalStateException("No magnetic field sensor"))
                }
            }
        }
    }

    companion object {

        private const val LOG_TAG = "SensorMagneticFieldRepository"
    }
}