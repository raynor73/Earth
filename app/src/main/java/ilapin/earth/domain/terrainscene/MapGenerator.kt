package ilapin.earth.domain.terrainscene

class MapGenerator(private val terrainScene: TerrainScene) {

    var mapWidth = 0
    var mapHeight = 0
    var noiseScale = 0f

    var octaves = 0
    var persistence = 0f
    var lacunarity = 0f

    fun generateMap() {
        val noiseMap = generateNoiseMap(mapWidth, mapHeight, noiseScale, octaves, persistence, lacunarity)

        terrainScene.drawNoiseMap(noiseMap)
    }
}