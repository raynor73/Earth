package ilapin.common.orientation

import ilapin.common.magneticfield.MagneticField
import org.joml.Matrix4fc

data class Orientation(
    val inclinationMatrix: Matrix4fc,
    val rotationMatrix: Matrix4fc,
    val azimuth: Float,
    val pitch: Float,
    val roll: Float,
    val magneticFieldAccuracy: MagneticField.Accuracy,
    val timestamp: Long
)