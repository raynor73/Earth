package ilapin.earth.domain.terrainscene

import org.joml.Vector2fc

class GenerateMapMessage(
    val drawMode: MapGenerator.DrawMode,
    val mapWidth: Int,
    val mapHeight: Int,
    val seed: Int,
    val noiseScale: Float,
    val octaves: Int,
    val persistence: Float,
    val lacunarity: Float,
    val offset: Vector2fc
)