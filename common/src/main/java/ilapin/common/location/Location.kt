package ilapin.common.location

data class Location(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val altitude: Double?,
    val accuracy: Float?
)