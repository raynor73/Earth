package ilapin.earth.ui.celestial_sphere

import ilapin.common.meshloader.MeshLoadingRepository
import ilapin.common.orientation.OrientationRepository
import ilapin.common.renderingengine.MeshRenderingRepository
import ilapin.common.renderingengine.RenderingSettingsRepository
import ilapin.common.renderingengine.TextureLoadingRepository
import ilapin.common.renderingengine.TextureRepository
import ilapin.earth.domain.celestial_sphere.CelestialSphere
import ilapin.engine3d.*
import io.reactivex.disposables.CompositeDisposable
import org.joml.Quaternionf
import org.joml.Vector3f

class CelestialSphereScene(
    renderingSettingsRepository: RenderingSettingsRepository,
    private val meshRenderingRepository: MeshRenderingRepository,
    private val textureRepository: TextureRepository,
    private val textureLoadingRepository: TextureLoadingRepository,
    private val meshLoadingRepository: MeshLoadingRepository,
    orientationRepository: OrientationRepository
) : Scene {
    //private val tmpQuaternion = Quaternionf()
    //private val tmpMatrix = Matrix4f()

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

    private val subscriptions = CompositeDisposable()

    //private val scrollController = ScrollController().apply { subscriptions.add(this) }

    //private val pixelDensityFactor = displayMetricsRepository.getPixelDensityFactor()

    private val celestialSphere = CelestialSphere(orientationRepository)

    init {
        renderingSettingsRepository.setClearColor(0f, 0f, 0f, 1.0f)
        renderingSettingsRepository.setAmbientColor(1f, 1f, 1f)

        setupPerspectiveCamera()

        initTextures()
        //initLights()

        initCelestialSphere()

        /*touchScreenRepository.touchEvents().subscribe(scrollController.touchEventsObserver)

        subscriptions.add(scrollController.scrollEvent.subscribe { scrollEvent ->
            val yAngle = Math.toRadians((scrollEvent.dx / pixelDensityFactor).toDouble())
            val xAngle = Math.toRadians((scrollEvent.dy / pixelDensityFactor).toDouble())
            tmpQuaternion.set(celestialSphereTransform.rotation)
            tmpQuaternion.rotateLocalY(yAngle.toFloat())
            tmpQuaternion.rotateLocalX(xAngle.toFloat())
            celestialSphereTransform.rotation = tmpQuaternion
        })*/
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
        textureLoadingRepository.loadTexture("2k_earth_daymap.jpg")
    }

    /*private fun initLights() {
        val light1GameObject = GameObject()
        light1GameObject.addComponent(TransformationComponent(
            Vector3f(),
            Quaternionf()
                .identity()
                .rotateX((Math.PI / 4).toFloat())
                .rotateY((Math.PI / 8).toFloat()),
            Vector3f(1f, 1f, 1f)
        ))
        val light1Component = DirectionalLightComponent(Vector3f(1f, 1f, 1f))
        light1GameObject.addComponent(light1Component)
        rootGameObject.addChild(light1GameObject)
        lightsRenderingRepository.addDirectionalLight(perspectiveCamera, light1Component)
    }*/

    private fun initCelestialSphere() {
        val gameObject = GameObject()

        gameObject.addComponent(celestialSphereTransform)

        val mesh = meshLoadingRepository.loadMesh("inverted_sphere.obj")
        gameObject.addComponent(mesh)

        gameObject.addComponent(MaterialComponent("2k_earth_daymap.jpg"))

        meshRenderingRepository.addMeshToRenderList(perspectiveCamera, mesh)

        rootGameObject.addChild(gameObject)

        subscriptions.add(celestialSphere.modelRotation.subscribe { rotation ->
            celestialSphereTransform.rotation = rotation
        })
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
        subscriptions.clear()
        celestialSphere.onCleared()
    }
}