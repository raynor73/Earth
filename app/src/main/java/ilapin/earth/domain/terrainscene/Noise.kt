package ilapin.earth.domain.terrainscene

import ilapin.common.math.inverseLerp
import ilapin.common.math.perlinnoise.ImprovedNoise

fun generateNoiseMap(
    mapWidth: Int,
    mapHeight: Int,
    scale: Float,
    octaves: Int,
    persistence: Float,
    lacunarity: Float
): Array<FloatArray> {
    val noiseMap = Array(mapWidth) { FloatArray(mapHeight) }

    val clampedScale = if (scale <= 0) {
        0.0001f
    } else {
        scale
    }

    var maxNoiseHeight = Float.MIN_VALUE
    var minNoiseHeight = Float.MAX_VALUE

    for (y: Int in 0 until mapHeight) {
        for (x: Int in 0 until mapWidth) {
            var amplitude = 1f
            var frequency = 1f
            var noiseHeight = 0f

            for (i: Int in 0 until octaves) {
                val sampleX = x / clampedScale * frequency
                val sampleY = y / clampedScale * frequency

                val perlinValue = ImprovedNoise.noise(sampleX.toDouble(), sampleY.toDouble(), 0.0).toFloat()
                noiseHeight += perlinValue * amplitude

                amplitude *= persistence
                frequency *= lacunarity
            }

            if (noiseHeight < minNoiseHeight) {
                minNoiseHeight = noiseHeight
            }
            if (noiseHeight > maxNoiseHeight) {
                maxNoiseHeight = noiseHeight
            }

            noiseMap[x][y] = noiseHeight
        }
    }

    for (y: Int in 0 until mapHeight) {
        for (x: Int in 0 until mapWidth) {
            noiseMap[x][y] = inverseLerp(minNoiseHeight, maxNoiseHeight, noiseMap[x][y])
        }
    }

    return noiseMap
}