package ilapin.earth.domain.terrainscene

class MapGenerator(private val terrainScene: TerrainScene) {

    var mapWidth = 0
    var mapHeight = 0
    var noiseScale = 0f

    fun generateMap() {
        val noiseMap = generateNoiseMap(mapWidth, mapHeight, noiseScale)

        terrainScene.drawNoiseMap(noiseMap)
    }
}