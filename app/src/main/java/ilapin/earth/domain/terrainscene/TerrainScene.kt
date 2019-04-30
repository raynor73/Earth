package ilapin.earth.domain.terrainscene

import ilapin.earth.domain.renderingengine.MeshRenderingRepository
import ilapin.earth.domain.renderingengine.RenderingSettingsRepository
import ilapin.earth.domain.renderingengine.TextureCreationRepository
import ilapin.earth.domain.time.TimeRepository
import ilapin.engine3d.*
import org.joml.Quaternionf
import org.joml.Vector3f

class TerrainScene(
    meshRenderingRepository: MeshRenderingRepository,
    renderingSettingsRepository: RenderingSettingsRepository,
    private val textureCreationRepository: TextureCreationRepository,
    private val timeRepository: TimeRepository
) {
    private val rootGameObject = GameObject()

    private var prevTimestamp: Long? = null

    val mapGenerator = MapGenerator(this)

    val camera = PerspectiveCameraComponent()

    init {
        val cameraGameObject = GameObject()
        cameraGameObject.addComponent(TransformationComponent(Vector3f(0f, 0f, 3f), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
        cameraGameObject.addComponent(camera)
        rootGameObject.addChild(cameraGameObject)

        val quadMesh = MeshFactory.createVerticalQuad()
        val quadGameObject = GameObject()
        quadGameObject.addComponent(quadMesh)
        quadGameObject.addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
        textureCreationRepository.createTexture("noiseTexture", 1, 1, intArrayOf(0xffffffff.toInt()))
        quadGameObject.addComponent(MaterialComponent("noiseTexture"))
        rootGameObject.addChild(quadGameObject)
        meshRenderingRepository.addMeshToRenderList(quadMesh)

        renderingSettingsRepository.setClearColor(0.2f, 0.2f, 0.2f, 0f)
        renderingSettingsRepository.setAmbientColor(1f, 1f, 1f)
    }

    fun update() {
        rootGameObject.update()
        val currentTimestamp = timeRepository.getTimestamp()
        prevTimestamp?.let {
            val dt = (currentTimestamp - it) / 1e9f
        }
        prevTimestamp = currentTimestamp
    }

    fun drawColorMap(width: Int, height: Int, colorMap: IntArray) {
        textureCreationRepository.createTexture("noiseTexture", width, height, colorMap)
    }

    fun drawNoiseMap(noiseMap: Array<FloatArray>) {
        val width = noiseMap.size
        val height = noiseMap[0].size

        val data = IntArray(width * height)

        for (y: Int in 0 until height) {
            for (x: Int in 0 until width) {
                val colorComponent = (0xff * noiseMap[x][y]).toInt()
                data[y * width + x] = 0xff000000.toInt() or
                        (colorComponent shl 16) or
                        (colorComponent shl 8) or
                        colorComponent
            }
        }

        drawColorMap(width, height, data)
    }
}