package ilapin.earth.domain.compass

import ilapin.common.meshloader.MeshLoadingRepository
import ilapin.common.orientation.OrientationRepository
import ilapin.common.renderingengine.*
import ilapin.earth.domain.camera.CameraInfo
import ilapin.engine3d.*
import io.reactivex.disposables.Disposable
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f

class CompassScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    orientationRepository: OrientationRepository,
    private val textureRepository: TextureRepository,
    private val specialTextureRepository: SpecialTextureRepository,
    private val meshRenderingRepository: MeshRenderingRepository,
    private val lightsRenderingRepository: LightsRenderingRepository,
    private val meshLoadingRepository: MeshLoadingRepository
) : Scene {

    private val rootGameObject = GameObject().apply {
        addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
    }

    private var subscription: Disposable? = null

    private val tmpVector = Vector3f()
    private val tmpQuaternion = Quaternionf()
    private val tmpMatrix = Matrix4f()

    private val arrowTransform = TransformationComponent(
        Vector3f(0f, -0.5f, -3f),
        Quaternionf().identity(),
        Vector3f(0.3f, 0.3f, 0.3f)
    )
    private val previewPlaneTransform = TransformationComponent(
        Vector3f(0f, 0f, -1f),
        Quaternionf().identity(),
        Vector3f(1f, 1f, 1f)
    )

    private val camera = PerspectiveCameraComponent()
    private val previewCamera = OrthoCameraComponent()

    override val cameras: List<CameraComponent> = listOf(previewCamera, camera)

    init {
        setupOrthoCamera()
        setupPerspectiveCamera()

        initTextures()

        initCompassArrow()
        initPreviewPlane()
        initLights()

        renderingSettingsRepository.setClearColor(0f, 0f, 0f, 0f)
        renderingSettingsRepository.setAmbientColor(0.3f, 0.3f, 0.3f)

        subscription = orientationRepository.orientation().subscribe { orientation ->
            tmpMatrix.set(orientation.rotationMatrix).invert()
            tmpQuaternion.setFromUnnormalized(tmpMatrix)
            arrowTransform.rotation = tmpQuaternion
        }
    }

    fun setupOrthoCamera() {
        val previewCameraGameObject = GameObject()
        previewCameraGameObject.addComponent(previewCamera)
        rootGameObject.addChild(previewCameraGameObject)
    }

    fun setupPerspectiveCamera() {
        val cameraGameObject = GameObject()
        cameraGameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        cameraGameObject.addComponent(camera)
        rootGameObject.addChild(cameraGameObject)
    }

    private fun initTextures() {
        textureRepository.createTexture("colorArrowRed", 1, 1, intArrayOf(0xffcd0e3a.toInt()))
        textureRepository.createTexture("colorArrowBlue", 1, 1, intArrayOf(0xff00a0b0.toInt()))
        textureRepository.createTexture("colorTargetMarker", 1, 1, intArrayOf(0xff00ff00.toInt()))
    }

    private fun initLights() {
        val light1GameObject = GameObject()
        light1GameObject.addComponent(TransformationComponent(
            Vector3f(),
            Quaternionf().identity().rotateZ((-Math.PI / 2).toFloat()),
            Vector3f(1f, 1f, 1f)
        ))
        val light1Component = DirectionalLightComponent(Vector3f(1f, 1f, 1f))
        light1GameObject.addComponent(light1Component)
        rootGameObject.addChild(light1GameObject)
        lightsRenderingRepository.addDirectionalLight(camera, light1Component)

        val light2GameObject = GameObject()
        light2GameObject.addComponent(TransformationComponent(
            Vector3f(),
            Quaternionf().identity().rotateZ((Math.PI / 2).toFloat()),
            Vector3f(1f, 1f, 1f)
        ))
        val light2Component = DirectionalLightComponent(Vector3f(0.8f, 0.8f, 0.8f))
        light2GameObject.addComponent(light2Component)
        rootGameObject.addChild(light2GameObject)
        lightsRenderingRepository.addDirectionalLight(camera, light2Component)
    }

    private fun initCompassArrow() {
        val arrowGameObject = GameObject()
        arrowGameObject.addComponent(arrowTransform)

        val northPointerGameObject = GameObject()
        val northPointerMesh = meshLoadingRepository.loadMesh("compass_arrow_north.obj")
        northPointerGameObject.addComponent(northPointerMesh)
        northPointerGameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        northPointerGameObject.addComponent(MaterialComponent("colorArrowRed"))
        meshRenderingRepository.addMeshToRenderList(camera, northPointerMesh)
        arrowGameObject.addChild(northPointerGameObject)

        val southPointerGameObject = GameObject()
        val southPointerMesh = meshLoadingRepository.loadMesh("compass_arrow_south.obj")
        southPointerGameObject.addComponent(southPointerMesh)
        southPointerGameObject.addComponent(
            TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f))
        )
        southPointerGameObject.addComponent(MaterialComponent("colorArrowBlue"))
        meshRenderingRepository.addMeshToRenderList(camera, southPointerMesh)
        arrowGameObject.addChild(southPointerGameObject)

        initTargetMarker(arrowGameObject)

        rootGameObject.addChild(arrowGameObject)
    }

    private fun initTargetMarker(compassArrow: GameObject) {
        val targetMarkerGameObject = GameObject()

        val mesh = MeshComponent(
            listOf(Vector3f(0f, 0f, 0f), Vector3f(0f, 0f, 10f)),
            listOf(Vector3f(0f, 0f, -1f), Vector3f(0f, 0f, 1f)),
            listOf(Vector2f(0f, 0f), Vector2f(0f, 0f)),
            listOf(0, 1)
        )
        targetMarkerGameObject.addComponent(mesh)
        targetMarkerGameObject.addComponent(
            MaterialComponent("colorTargetMarker", isDoubleSided = true, isWireframe = true, isUnlit = true)
        )
        targetMarkerGameObject.addComponent(TransformationComponent(
            Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)
        ))

        meshRenderingRepository.addMeshToRenderList(camera, mesh)

        compassArrow.addChild(targetMarkerGameObject)
    }

    fun onCameraInfoUpdate(cameraInfo: CameraInfo) {
        tmpVector.set(previewPlaneTransform.scale)
        if (cameraInfo.sensorOrientation == 90 || cameraInfo.sensorOrientation == 270) {
            tmpVector.x = cameraInfo.previewSize.height.toFloat()
            tmpVector.y = cameraInfo.previewSize.width.toFloat()
        } else {
            tmpVector.x = cameraInfo.previewSize.width.toFloat()
            tmpVector.y = cameraInfo.previewSize.height.toFloat()
        }
        previewPlaneTransform.scale = tmpVector

        tmpQuaternion.identity()
        tmpQuaternion.rotateZ((-Math.toRadians(cameraInfo.sensorOrientation.toDouble())).toFloat())
        previewPlaneTransform.rotation = tmpQuaternion
    }

    override fun update() {}

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        updatePerspectiveCameraConfig(width, height)
        updateOrthoCameraConfig(width, height)
    }

    override fun onCleared() {
        subscription?.dispose()
    }

    private fun initPreviewPlane() {
        val previewPlaneGameObject = GameObject()
        val previewPlaneMesh = MeshComponent(
            listOf(Vector3f(-0.5f, 0.5f, 0f), Vector3f(0.5f, 0.5f, 0f), Vector3f(0.5f, -0.5f, 0f), Vector3f(-0.5f, -0.5f, 0f)),
            listOf(Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f), Vector3f(0f, 0f, 1f)),
            listOf(Vector2f(0f, 0f), Vector2f(1f, 0f), Vector2f(1f, 1f), Vector2f(0f, 1f)),
            listOf(0, 1, 2, 2, 3, 0)
        )
        previewPlaneGameObject.addComponent(previewPlaneMesh)
        previewPlaneGameObject.addComponent(previewPlaneTransform)
        previewPlaneGameObject.addComponent(MaterialComponent(specialTextureRepository.getDeviceCameraTextureName(), true))
        rootGameObject.addChild(previewPlaneGameObject)
        meshRenderingRepository.addMeshToRenderList(previewCamera, previewPlaneMesh)
    }

    private fun updateOrthoCameraConfig(width: Int, height: Int) {
        val halfWidth = width / 2f
        val halfHeight = height / 2f
        previewCamera.config = OrthoCameraComponent.Config(
            -halfWidth, halfWidth,
            -halfHeight, halfHeight,
            0.1f, 1000f
        )
    }

    private fun updatePerspectiveCameraConfig(width: Int, height: Int) {
        camera.config = PerspectiveCameraComponent.Config(
            45f,
            width.toFloat() / height.toFloat(),
            0.1f,
            1000f
        )
    }
}