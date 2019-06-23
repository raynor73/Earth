package ilapin.earth.domain.compass

import ilapin.common.orientation.OrientationRepository
import ilapin.common.renderingengine.MeshRenderingRepository
import ilapin.common.renderingengine.RenderingSettingsRepository
import ilapin.common.renderingengine.TextureRepository
import ilapin.engine3d.*
import io.reactivex.disposables.Disposable
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f

class CompassScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    orientationRepository: OrientationRepository,
    textureRepository: TextureRepository,
    meshRenderingRepository: MeshRenderingRepository
) : Scene {

    private val rootGameObject = GameObject()

    private var subscription: Disposable? = null

    private val tmpQuaternion = Quaternionf()
    private val tmpMatrix = Matrix4f()

    private val arrowTransform = TransformationComponent(
        Vector3f(0f, -0.5f, -3f),
        Quaternionf().identity(),
        Vector3f(1f, 1f, 1f)
    )

    private val camera = PerspectiveCameraComponent()
    override val cameras: List<CameraComponent> = listOf(camera)

    init {
        val cameraGameObject = GameObject()
        cameraGameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        cameraGameObject.addComponent(camera)
        rootGameObject.addChild(cameraGameObject)

        val arrowGameObject = GameObject()
        val arrowMesh = MeshComponent(
            listOf(Vector3f(0f, 1f, 0f), Vector3f(0.25f, 0f, 0f), Vector3f(-0.25f, 0f, 0f)),
            listOf(Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f)),
            listOf(Vector2f(0.5f, 0f), Vector2f(1f, 1f), Vector2f(0f, 1f)),
            listOf(0, 1, 2)
        )
        arrowGameObject.addComponent(arrowMesh)
        arrowGameObject.addComponent(arrowTransform)
        arrowGameObject.addComponent(MaterialComponent("colorWhite", true))
        rootGameObject.addChild(arrowGameObject)
        meshRenderingRepository.addMeshToRenderList(camera, arrowMesh)
        textureRepository.createTexture("colorWhite", 1, 1, intArrayOf(0xffffffff.toInt()))

        renderingSettingsRepository.setClearColor(0.2f, 0.2f, 0.2f, 0f)
        renderingSettingsRepository.setAmbientColor(1f, 1f, 1f)

        subscription = orientationRepository.orientation().subscribe { orientation ->
            tmpMatrix.set(orientation.rotationMatrix).invert()
            tmpQuaternion.setFromUnnormalized(tmpMatrix)
            arrowTransform.rotation = tmpQuaternion
        }
    }

    override fun update() {}

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        camera.config = PerspectiveCameraComponent.Config(
            45f,
            width.toFloat() / height.toFloat(),
            0.1f,
            1000f
        )
    }

    override fun onCleared() {
        subscription?.dispose()
    }
}