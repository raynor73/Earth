package ilapin.earth.frameworkdependent.acceleration

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import ilapin.earth.domain.acceleration.Acceleration
import ilapin.earth.domain.acceleration.AccelerationRepository
import io.reactivex.Observable

class SensorAccelerationRepository(private val sensorManager: SensorManager) : AccelerationRepository {

    override fun acceleration(): Observable<Acceleration> {
        return Observable.create { emitter ->
            val accelerometerListener = object : SensorEventListener {

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // do nothing
                }

                override fun onSensorChanged(event: SensorEvent) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(
                            Acceleration(
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
                sensorManager.unregisterListener(accelerometerListener)
            }

            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            if (accelerometer != null) {
                sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            } else {
                if (!emitter.isDisposed) {
                    emitter.onError(IllegalStateException("No accelerometer"))
                }
            }
        }
    }
}