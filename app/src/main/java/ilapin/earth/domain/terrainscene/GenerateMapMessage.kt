package ilapin.earth.domain.terrainscene

class GenerateMapMessage(
    val mapWidth: Int,
    val mapHeight: Int,
    val noiseScale: Float,
    val octaves: Int,
    val persistence: Float,
    val lacunarity: Float
)