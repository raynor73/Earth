package ilapin.earth.domain.compass

import ilapin.common.acceleration.AccelerationRepository
import ilapin.common.meshloader.MeshLoadingRepository
import ilapin.common.orientation.OrientationRepository
import ilapin.common.renderingengine.*
import ilapin.earth.domain.camera.CameraInfo
import ilapin.engine3d.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector2f
import org.joml.Vector3f
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class CompassScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    orientationRepository: OrientationRepository,
    accelerationRepository: AccelerationRepository,
    private val textureRepository: TextureRepository,
    private val specialTextureRepository: SpecialTextureRepository,
    private val meshRenderingRepository: MeshRenderingRepository,
    private val lightsRenderingRepository: LightsRenderingRepository,
    private val meshLoadingRepository: MeshLoadingRepository
) : Scene {

    private val rootGameObject = GameObject().apply {
        addComponent(TransformationComponent(Vector3f(), Quaternionf().identity(), Vector3f(1f, 1f, 1f)))
    }

    private val spiritLevelStaticPartGameObject = GameObject()
    private val spiritLevelDynamicPartGameObject = GameObject()

    private val subscriptions = CompositeDisposable()
    private var blinkingIntervalSubscription: Disposable? = null

    @Volatile
    private var isSpiritLevelVisible = true

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
    private val spiritLevelStaticPartTransform = TransformationComponent(
        Vector3f(2f, 1f, -10f),
        Quaternionf().identity(),
        Vector3f(1f, 1f, 1f)
    )
    private val spiritLevelDynamicPartTransform = TransformationComponent(
        Vector3f(2f, 1f, -10f),
        Quaternionf().identity(),
        Vector3f(1f, 1f, 1f)
    )

    private val colorOkMaterial = MaterialComponent("colorOk", isDoubleSided = true, isWireframe = true, isUnlit = true)
    private val colorWarningMaterial = MaterialComponent("colorWarning", isDoubleSided = true, isWireframe = true, isUnlit = true)
    private val colorAlertMaterial = MaterialComponent("colorAlert", isDoubleSided = true, isWireframe = true, isUnlit = true)

    private val camera = PerspectiveCameraComponent()
    private val previewCamera = OrthoCameraComponent()

    private val rotationMatrixSmoother = RotationMatrixSmoother(10)
    private val vectorSmoother = VectorSmoother(10)

    override val cameras: List<CameraComponent> = listOf(previewCamera, camera)

    init {
        setupOrthoCamera()
        setupPerspectiveCamera()

        initTextures()

        initCompassArrow()
        initTargetMarker()
        initSpiritLevel()
        initPreviewPlane()
        initLights()

        renderingSettingsRepository.setClearColor(0f, 0f, 0f, 0f)
        renderingSettingsRepository.setAmbientColor(0.1f, 0.1f, 0.1f)

        orientationRepository.orientation().map { it.rotationMatrix }.subscribe(rotationMatrixSmoother)
        accelerationRepository.acceleration().map { tmpVector.set(it.x, it.y, it.z) }.subscribe(vectorSmoother)

        subscriptions.add(rotationMatrixSmoother.smoothedRotationMatrix.subscribe { rotationMatrix ->
            tmpMatrix.set(rotationMatrix).invert()
            tmpQuaternion.setFromUnnormalized(tmpMatrix)
            arrowTransform.rotation = tmpQuaternion
        })
        subscriptions.add(vectorSmoother.smoothedVector.subscribe { vector ->
            tmpVector.set(0f, 1f, 0f)
            tmpQuaternion.identity().rotateTo(tmpVector, vector)
            spiritLevelDynamicPartTransform.rotation = tmpQuaternion

            tmpVector.set(vector).normalize()
            when {
                abs(tmpVector.x) < 0.1f -> {
                    setSpiritLevelMaterial(colorOkMaterial)
                    stopSpiritLevelBlinking()
                }
                abs(tmpVector.x) < 0.3f -> {
                    setSpiritLevelMaterial(colorWarningMaterial)
                    stopSpiritLevelBlinking()
                }
                else -> {
                    setSpiritLevelMaterial(colorAlertMaterial)
                    startSpiritLevelBlinking()
                }
            }
        })
    }

    private fun startSpiritLevelBlinking() {
        if (blinkingIntervalSubscription != null) {
            return
        }

        /*object : Scheduler() {
            override fun createWorker(): Worker {
                return object : Worker() {

                    override fun isDisposed(): Boolean {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun dispose() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }
            }
        }*/

        blinkingIntervalSubscription = Observable.interval(500, TimeUnit.MILLISECONDS).subscribe {
            isSpiritLevelVisible = !isSpiritLevelVisible
        }
    }

    private fun stopSpiritLevelBlinking() {
        blinkingIntervalSubscription?.let {
            it.dispose()
            blinkingIntervalSubscription = null
            isSpiritLevelVisible = true
        }
    }

    private fun setSpiritLevelMaterial(material: MaterialComponent) {
        spiritLevelStaticPartGameObject.getComponent(MaterialComponent::class.java)?.let {
            spiritLevelStaticPartGameObject.removeComponent(it)
        }
        spiritLevelStaticPartGameObject.addComponent(material)

        spiritLevelDynamicPartGameObject.getComponent(MaterialComponent::class.java)?.let {
            spiritLevelDynamicPartGameObject.removeComponent(it)
        }
        spiritLevelDynamicPartGameObject.addComponent(material)
    }

    private fun initSpiritLevel() {
        spiritLevelStaticPartGameObject.addComponent(colorOkMaterial)
        spiritLevelStaticPartGameObject.addComponent(spiritLevelStaticPartTransform)
        val staticPartMesh = meshLoadingRepository.loadMesh("grid.obj")
        spiritLevelStaticPartGameObject.addComponent(staticPartMesh)
        meshRenderingRepository.addMeshToRenderList(camera, staticPartMesh)

        rootGameObject.addChild(spiritLevelStaticPartGameObject)

        spiritLevelDynamicPartGameObject.addComponent(colorOkMaterial)
        spiritLevelDynamicPartGameObject.addComponent(spiritLevelDynamicPartTransform)
        val dynamicPartMesh = meshLoadingRepository.loadMesh("grid.obj")
        spiritLevelDynamicPartGameObject.addComponent(dynamicPartMesh)
        meshRenderingRepository.addMeshToRenderList(camera, dynamicPartMesh)

        rootGameObject.addChild(spiritLevelDynamicPartGameObject)
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

        textureRepository.createTexture("colorOk", 1, 1, intArrayOf(0xff00ff00.toInt()))
        textureRepository.createTexture("colorWarning", 1, 1, intArrayOf(0xffffff00.toInt()))
        textureRepository.createTexture("colorAlert", 1, 1, intArrayOf(0xffff0000.toInt()))
    }

    private fun initLights() {
        val light1GameObject = GameObject()
        light1GameObject.addComponent(TransformationComponent(
            Vector3f(),
            Quaternionf()
                .identity()
                .rotateX((Math.PI / 2).toFloat())
                .rotateY((Math.PI / 8).toFloat()),
            Vector3f(1f, 1f, 1f)
        ))
        val light1Component = DirectionalLightComponent(Vector3f(1f, 1f, 1f))
        light1GameObject.addComponent(light1Component)
        rootGameObject.addChild(light1GameObject)
        lightsRenderingRepository.addDirectionalLight(camera, light1Component)

        val light2GameObject = GameObject()
        light2GameObject.addComponent(TransformationComponent(
            Vector3f(),
            Quaternionf()
                .identity()
                .rotateZ((Math.PI / 8).toFloat()),
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

        rootGameObject.addChild(arrowGameObject)
    }

    private fun initTargetMarker() {
        val targetMarkerGameObject = GameObject()

        val mesh = MeshComponent(
            listOf(Vector3f(0f, 0f, 0f), Vector3f(0f, 2f, 0f)),
            listOf(Vector3f(0f, 0f, -1f), Vector3f(0f, 0f, 1f)),
            listOf(Vector2f(0f, 0f), Vector2f(0f, 0f)),
            listOf(0, 1)
        )
        targetMarkerGameObject.addComponent(mesh)
        targetMarkerGameObject.addComponent(
            MaterialComponent("colorTargetMarker", isDoubleSided = true, isWireframe = true, isUnlit = true)
        )
        targetMarkerGameObject.addComponent(TransformationComponent(
            Vector3f(0f, -0.5f, -3f), Quaternionf().identity(), Vector3f(1f, 1f, 1f)
        ))

        meshRenderingRepository.addMeshToRenderList(camera, mesh)

        rootGameObject.addChild(targetMarkerGameObject)
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

    override fun update() {
        spiritLevelStaticPartGameObject.isEnabled = isSpiritLevelVisible
        spiritLevelDynamicPartGameObject.isEnabled = isSpiritLevelVisible
    }

    override fun onScreenConfigUpdate(width: Int, height: Int) {
        updatePerspectiveCameraConfig(width, height)
        updateOrthoCameraConfig(width, height)
    }

    override fun onCleared() {
        subscriptions.dispose()
        rotationMatrixSmoother.dispose()
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