package ilapin.earth.domain.terrainscene

import org.joml.Vector2f

class MapGenerator(private val terrainScene: TerrainScene) {

    private val regions = arrayOf(
        TerrainType("Water", 0.4f, 0xff4372d0.toInt()),
        TerrainType("Land", 1f, 0xff569717.toInt())
    )

    var drawMode = DrawMode.NOISE_MAP

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

        when (drawMode) {
            DrawMode.NOISE_MAP -> terrainScene.drawNoiseMap(noiseMap)
            DrawMode.COLOR_MAP -> {
                val colorMap = IntArray(mapWidth * mapHeight)
                for (y: Int in 0 until mapHeight) {
                    for (x: Int in 0 until mapWidth) {
                        val currentHeight = noiseMap[x][y]
                        for (i: Int in 0 until regions.size) {
                            if (currentHeight <= regions[i].height) {
                                colorMap[y * mapWidth + x] = regions[i].color
                                break
                            }
                        }
                    }
                }

                terrainScene.drawColorMap(mapWidth, mapHeight, colorMap)
            }
        }

    }

    enum class DrawMode {
        NOISE_MAP, COLOR_MAP
    }
}