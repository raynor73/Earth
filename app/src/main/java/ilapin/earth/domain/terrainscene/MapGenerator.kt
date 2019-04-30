package ilapin.earth.domain.terrainscene

import org.joml.Vector2f

class MapGenerator(private val terrainScene: TerrainScene) {

    var mapWidth = 0
    var mapHeight = 0
    var noiseScale = 0f

    var octaves = 0
    var persistence = 0f
    var lacunarity = 0f

    var seed = 0
    val offset = Vector2f()

    fun generateMap() {
        val noiseMap = generateNoiseMap(
            mapWidth,
            mapHeight,
            seed,
            noiseScale,
            octaves,
            persistence,
            lacunarity,
            offset
        )

        terrainScene.drawNoiseMap(noiseMap)
    }
}