package ilapin.earth.frameworkdependent.magneticfield

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ilapin.earth.domain.magneticfield.MagneticField
import ilapin.earth.domain.magneticfield.MagneticFieldRepository
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
}