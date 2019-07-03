package ilapin.common.magneticfield

data class MagneticField(
    val x: Float,
    val y: Float,
    val z: Float,
    val accuracy: Accuracy,
    val timestamp: Long
) {

    enum class Accuracy {
        LOW, MEDIUM, HIGH
    }
}