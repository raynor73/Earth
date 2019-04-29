package ilapin.earth.domain.terrainscene

import ilapin.common.math.perlinnoise.ImprovedNoise

fun generateNoiseMap(mapWidth: Int, mapHeight: Int, scale: Float): Array<FloatArray> {
    val noiseMap = Array(mapWidth) { FloatArray(mapHeight) }

    val clampedScale = if (scale <= 0) {
        0.0001f
    } else {
        scale
    }

    for (y: Int in 0 until mapHeight) {
        for (x: Int in 0 until mapWidth) {
            val sampleX = x / clampedScale
            val sampleY = y / clampedScale

            val perlinValue = ImprovedNoise.noise(sampleX.toDouble(), sampleY.toDouble(), 0.0).toFloat()
            noiseMap[x][y] = perlinValue
        }
    }

    return noiseMap
}