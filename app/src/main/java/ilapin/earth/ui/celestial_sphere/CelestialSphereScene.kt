package ilapin.earth.ui.celestial_sphere

import ilapin.common.meshloader.MeshLoadingRepository
import ilapin.common.renderingengine.MeshRenderingRepository
import ilapin.common.renderingengine.RenderingSettingsRepository
import ilapin.common.renderingengine.TextureRepository
import ilapin.engine3d.*
import org.joml.Quaternionf
import org.joml.Vector3f

class CelestialSphereScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    private val meshRenderingRepository: MeshRenderingRepository,
    private val textureRepository: TextureRepository,
    private val meshLoadingRepository: MeshLoadingRepository
) : Scene {

    private val rootGameObject = GameObject().apply {
        addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
    }

    private val celestialSphereTransform = TransformationComponent(
        Vector3f(0f, -0.5f, -3f),
        Quaternionf().identity(),
        Vector3f(1f, 1f, 1f)
    )

    private val perspectiveCamera = PerspectiveCameraComponent()

    override val cameras: List<CameraComponent> = listOf(perspectiveCamera)

    init {
        renderingSettingsRepository.setClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        renderingSettingsRepository.setAmbientColor(0.1f, 0.1f, 0.1f)

        setupPerspectiveCamera()

        initTextures()

        initCelestialSphere()
    }

    fun setupPerspectiveCamera() {
        val cameraGameObject = GameObject()
        cameraGameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        cameraGameObject.addComponent(perspectiveCamera)
        rootGameObject.addChild(cameraGameObject)
    }

    private fun initTextures() {
        textureRepository.createTexture("colorGreen", 1, 1, intArrayOf(0xff00ff00.toInt()))
    }

    private fun initCelestialSphere() {
        val gameObject = GameObject()

        gameObject.addComponent(celestialSphereTransform)

        val mesh = meshLoadingRepository.loadMesh("earth.obj")
        gameObject.addComponent(mesh)

        gameObject.addComponent(
            MaterialComponent("colorGreen", isDoubleSided = true, isWireframe = true, isUnlit = true)
        )

        meshRenderingRepository.addMeshToRenderList(perspectiveCamera, mesh)

        rootGameObject.addChild(gameObject)
    }

    override fun update() {
        rootGameObject.update()
    }

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        perspectiveCamera.config = PerspectiveCameraComponent.Config(
            45f,
            width.toFloat() / height.toFloat(),
            0.1f,
            1000f
        )
    }

    override fun onCleared() {
        // do nothing
    }
}