package ilapin.earth.domain.terrain

import ilapin.common.renderingengine.MeshRenderingRepository
import ilapin.common.renderingengine.RenderingSettingsRepository
import ilapin.common.renderingengine.TextureCreationRepository
import ilapin.common.time.TimeRepository
import ilapin.engine3d.*
import org.joml.Quaternionf
import org.joml.Vector3f

class TerrainScene(
    private val meshRenderingRepository: MeshRenderingRepository,
    renderingSettingsRepository: RenderingSettingsRepository,
    private val textureCreationRepository: TextureCreationRepository,
    private val timeRepository: TimeRepository
) : Scene {
    private val rootGameObject = GameObject()

    private var prevTimestamp: Long? = null

    private val quadMesh: MeshComponent
    private val terrainMesh: MeshComponent? = null
    private val cameraTransform: TransformationComponent

    val mapGenerator = MapGenerator(this)

    private val camera = PerspectiveCameraComponent()
    override val cameras: List<CameraComponent> = listOf(camera)

    init {
        val cameraGameObject = GameObject()
        cameraTransform = TransformationComponent(Vector3f(0f, 0f, 3f), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        cameraGameObject.addComponent(cameraTransform)
        cameraGameObject.addComponent(camera)
        rootGameObject.addChild(cameraGameObject)

        quadMesh = MeshFactory.createVerticalQuad()
        val quadGameObject = GameObject()
        quadGameObject.addComponent(quadMesh)
        quadGameObject.addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
        textureCreationRepository.createTexture("noiseTexture", 1, 1, intArrayOf(0xffffffff.toInt()))
        quadGameObject.addComponent(MaterialComponent("noiseTexture"))
        rootGameObject.addChild(quadGameObject)
        meshRenderingRepository.addMeshToRenderList(camera, quadMesh)

        renderingSettingsRepository.setClearColor(0.2f, 0.2f, 0.2f, 0f)
        renderingSettingsRepository.setAmbientColor(1f, 1f, 1f)
    }

    override fun update() {
        rootGameObject.update()
        val currentTimestamp = timeRepository.getTimestamp()
        prevTimestamp?.let {
            val dt = (currentTimestamp - it) / 1e9f
        }
        prevTimestamp = currentTimestamp
    }

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

    fun drawMesh(noiseMap: Array<FloatArray>) {
        val terrainMesh = MeshFactory.createTerrainMesh(noiseMap)
        val terrainGameObject = GameObject()
        terrainGameObject.addComponent(terrainMesh)
        terrainGameObject.addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
        textureCreationRepository.createTexture("noiseTexture", 1, 1, intArrayOf(0xffffffff.toInt()))
        terrainGameObject.addComponent(MaterialComponent("noiseTexture"))
        rootGameObject.addChild(terrainGameObject)

    }

    override fun onCleared() {}

    private fun demonstrateQuad() {
        cameraTransform.position = Vector3f(0f, 0f, 3f)
        cameraTransform.rotation = Quaternionf().identity()
        cameraTransform.scale = Vector3f(1f, 1f, 1f)
        terrainMesh?.let { meshRenderingRepository.removeMeshFromRenderList(camera, it) }
        meshRenderingRepository.addMeshToRenderList(camera, quadMesh)
    }

    private fun demonstrateTerrain() {
        cameraTransform.position = Vector3f(-60f, 10f, 60f)
        cameraTransform.rotation = Quaternionf().identity().rotateY((-Math.PI / 2).toFloat())
        cameraTransform.scale = Vector3f(1f, 1f, 1f)
        meshRenderingRepository.removeMeshFromRenderList(camera, quadMesh)
        terrainMesh?.let { meshRenderingRepository.addMeshToRenderList(camera, it) }
    }
}