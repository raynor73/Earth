package ilapin.common.android.orientation

import android.hardware.SensorManager
import ilapin.common.acceleration.Acceleration
import ilapin.common.acceleration.AccelerationRepository
import ilapin.common.magneticfield.MagneticField
import ilapin.common.magneticfield.MagneticFieldRepository
import ilapin.common.orientation.Orientation
import ilapin.common.orientation.OrientationRepository
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.joml.Matrix4f

class SoftwareOrientationRepository(
    private val accelerationRepository: AccelerationRepository,
    private val magneticFieldRepository: MagneticFieldRepository
) : OrientationRepository {

    private val rMatrixData = FloatArray(16)
    private val iMatrixData = FloatArray(16)

    private val accelerationData = FloatArray(3)
    private val magneticFieldData = FloatArray(3)
    private val orientationData = FloatArray(3)

    override fun orientation(): Observable<Orientation> {
        return Observable.combineLatest(
            accelerationRepository.acceleration(),
            magneticFieldRepository.magneticField(),
            BiFunction<Acceleration, MagneticField, Orientation> { acceleration, magneticField ->
                accelerationData[0] = acceleration.x
                accelerationData[1] = acceleration.y
                accelerationData[2] = acceleration.z

                magneticFieldData[0] = magneticField.x
                magneticFieldData[1] = magneticField.y
                magneticFieldData[2] = magneticField.z

                SensorManager.getRotationMatrix(rMatrixData, iMatrixData, accelerationData, magneticFieldData)
                SensorManager.getOrientation(rMatrixData, orientationData)

                val rotationMatrix = Matrix4f()
                val inclinationMatrix = Matrix4f()
                rotationMatrix.set(rMatrixData).invert()
                inclinationMatrix.set(iMatrixData).invert()

                Orientation(
                    inclinationMatrix,
                    rotationMatrix,
                    orientationData[0],
                    orientationData[1],
                    orientationData[2],
                    maxOf(acceleration.timestamp, magneticField.timestamp)
                )
            }
        )
    }
}