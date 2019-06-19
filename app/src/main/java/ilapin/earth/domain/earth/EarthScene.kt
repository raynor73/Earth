package ilapin.earth.domain.earth

import ilapin.common.meshloader.MeshLoadingRepository
import ilapin.common.renderingengine.MeshRenderingRepository
import ilapin.common.renderingengine.RenderingSettingsRepository
import ilapin.common.renderingengine.TextureLoadingRepository
import ilapin.common.time.TimeRepository
import ilapin.engine3d.GameObject
import ilapin.engine3d.MaterialComponent
import ilapin.engine3d.PerspectiveCameraComponent
import ilapin.engine3d.TransformationComponent
import org.joml.Quaternionf
import org.joml.Vector3f

class EarthScene(
    renderingRepository: MeshRenderingRepository,
    renderingSettingsRepository: RenderingSettingsRepository,
    textureLoadingRepository: TextureLoadingRepository,
    meshLoadingRepository: MeshLoadingRepository,
    private val timeRepository: TimeRepository
) {
    private val rootGameObject = GameObject()

    private var prevTimestamp: Long? = null

    private val cameraTransform: TransformationComponent
    private var cameraAzimuth = 0f
    private var elapsedTime = 0f
    private val originCameraPosition = Vector3f(0f, 0f, 2.7f)

    private val tmpVector = Vector3f()
    private val tmpQuaternion = Quaternionf()

    val camera = PerspectiveCameraComponent()

    init {
        val cameraGameObject = GameObject()
        cameraTransform = TransformationComponent(originCameraPosition, Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        cameraGameObject.addComponent(cameraTransform)
        cameraGameObject.addComponent(camera)
        rootGameObject.addChild(cameraGameObject)

        val earthMesh = meshLoadingRepository.loadMesh("earth.obj")
        val earthGameObject = GameObject()
        earthGameObject.addComponent(earthMesh)
        earthGameObject.addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
        val textureName = "2k_earth_daymap.jpg"
        earthGameObject.addComponent(MaterialComponent(textureName))
        textureLoadingRepository.loadTexture(textureName)
        rootGameObject.addChild(earthGameObject)
        renderingRepository.addMeshToRenderList(camera, earthMesh)

        renderingSettingsRepository.setClearColor(0.2f, 0.2f, 0.2f, 0f)
        renderingSettingsRepository.setAmbientColor(1f, 1f, 1f)
    }

    fun update() {
        rootGameObject.update()
        val currentTimestamp = timeRepository.getTimestamp()
        prevTimestamp?.let {
            val dt = (currentTimestamp - it) / 1e9f
            elapsedTime += dt

            cameraAzimuth = (Math.PI / 30 * elapsedTime).toFloat()

            originCameraPosition.rotateY(cameraAzimuth, tmpVector)
            cameraTransform.position = tmpVector
            cameraTransform.rotation

            tmpQuaternion.identity()
            tmpQuaternion.rotateY(cameraAzimuth)
            cameraTransform.rotation = tmpQuaternion
        }
        prevTimestamp = currentTimestamp
    }
}