package ilapin.earth.domain.terrain

import ilapin.common.math.inverseLerp
import ilapin.common.math.perlinnoise.ImprovedNoise
import org.joml.Vector2f
import org.joml.Vector2fc
import kotlin.random.Random

fun generateNoiseMap(
    mapWidth: Int,
    mapHeight: Int,
    seed: Int,
    scale: Float,
    octaves: Int,
    persistence: Float,
    lacunarity: Float,
    offset: Vector2fc
): Array<FloatArray> {
    val noiseMap = Array(mapWidth) { FloatArray(mapHeight) }

    val random = Random(seed)
    val octaveOffsets = Array(octaves) { Vector2f() }
    octaveOffsets.forEach {
        it.set(
            random.nextInt(-100000, 100000).toFloat() + offset.x(),
            random.nextInt(-100000, 100000).toFloat() + offset.y()
        )
    }

    val clampedScale = if (scale <= 0) {
        0.0001f
    } else {
        scale
    }

    var maxNoiseHeight = Float.MIN_VALUE
    var minNoiseHeight = Float.MAX_VALUE

    val halfWidth = mapWidth / 2f
    val halfHeight = mapHeight / 2f

    for (y: Int in 0 until mapHeight) {
        for (x: Int in 0 until mapWidth) {
            var amplitude = 1f
            var frequency = 1f
            var noiseHeight = 0f

            for (i: Int in 0 until octaves) {
                val sampleX = (x - halfWidth) / clampedScale * frequency + octaveOffsets[i].x
                val sampleY = (y - halfHeight) / clampedScale * frequency + octaveOffsets[i].y

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