package ilapin.earth.domain.orientation

import org.joml.Matrix4fc

data class Orientation(
    val inclinationMatrix: Matrix4fc,
    val rotationMatrix: Matrix4fc,
    val azimuth: Float,
    val pitch: Float,
    val roll: Float,
    val timestamp: Long
)