package ilapin.earth.domain.camera

import ilapin.common.renderingengine.MeshRenderingRepository
import ilapin.common.renderingengine.RenderingSettingsRepository
import ilapin.common.renderingengine.TextureCreationRepository
import ilapin.engine3d.*
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f

class CameraScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    textureCreationRepository: TextureCreationRepository,
    meshRenderingRepository: MeshRenderingRepository
) : Scene {

    private val tmpVector = Vector3f()

    private val rootGameObject = GameObject()

    private val previewCamera = OrthoCameraComponent()
    //private val compassCamera = PerspectiveCameraComponent()

    private val previewPlaneTransform = TransformationComponent(
        Vector3f(0f, 0f, -1f),
        Quaternionf().identity(),
        Vector3f(1f, 1f, 1f)
    )

    override val cameras: List<CameraComponent> = listOf(previewCamera)

    init {
        val previewCameraGameObject = GameObject()
        previewCameraGameObject.addComponent(previewCamera)
        rootGameObject.addChild(previewCameraGameObject)

        val previewPlaneGameObject = GameObject()
        val previewPlaneMesh = MeshComponent(
            listOf(Vector3f(-25f, 25f, 0f), Vector3f(25f, 25f, 0f), Vector3f(25f, -25f, 0f), Vector3f(-25f, -25f, 0f)),
            listOf(Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f)),
            listOf(Vector2f(0f, 0f), Vector2f(0f, 1f), Vector2f(1f, 1f), Vector2f(0f, 1f)),
            listOf(0, 1, 2, 2, 3, 0)
        )
        previewPlaneGameObject.addComponent(previewPlaneMesh)
        previewPlaneGameObject.addComponent(previewPlaneTransform)
        previewPlaneGameObject.addComponent(MaterialComponent("colorWhite", true))
        rootGameObject.addChild(previewPlaneGameObject)
        meshRenderingRepository.addMeshToRenderList(previewCamera, previewPlaneMesh)
        textureCreationRepository.createTexture("colorWhite", 1, 1, intArrayOf(0xffffffff.toInt()))

        renderingSettingsRepository.setClearColor(0.2f, 0.2f, 0.2f, 0f)
        renderingSettingsRepository.setAmbientColor(1f, 1f, 1f)
    }

    fun applySizeModifier(modifier: Int) {
        val clampedModifier = when {
            modifier > 100 -> 100
            modifier < 0 -> 0
            else -> modifier
        }

        tmpVector.x = 1 + modifier / 100f
        tmpVector.y = 1 + modifier / 200f
        previewPlaneTransform.scale = tmpVector
    }

    override fun update() {
        // do nothing
    }

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        previewCamera.config = OrthoCameraComponent.Config(
            -halfWidth, halfWidth,
            -halfHeight, halfHeight,
            0.1f, 1000f
        )
    }

    override fun onCleared() {
        // do nothing
    }
}