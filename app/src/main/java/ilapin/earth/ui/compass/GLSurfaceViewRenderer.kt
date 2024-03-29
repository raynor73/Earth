package ilapin.earth.ui.compass

import android.content.Context
import ilapin.common.acceleration.AccelerationFromMessageQueueRepository
import ilapin.common.android.meshloader.ObjMeshLoadingRepository
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.android.time.LocalTimeRepository
import ilapin.common.messagequeue.MessageQueue
import ilapin.common.orientation.OrientationFromMessageQueueRepository
import ilapin.earth.domain.camera.CameraActivator
import ilapin.earth.domain.camera.CameraInfo
import ilapin.earth.domain.compass.CompassScene
import ilapin.earth.frameworkdependent.camera.LocalCameraRepository
import ilapin.engine3d.Scene
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(private val context: Context) : BaseGLSurfaceRenderer(context) {

    private val isCameraPermissionGrantedSubject = PublishSubject.create<Boolean>()
    private lateinit var cameraActivator: CameraActivator

    private var subscriptions = CompositeDisposable()

    override fun createScene(messageQueue: MessageQueue): Scene {
        val scene = CompassScene(
            renderingEngine,
            OrientationFromMessageQueueRepository(messageQueue),
            AccelerationFromMessageQueueRepository(messageQueue),
            LocalTimeRepository(),
            renderingEngine,
            renderingEngine,
            renderingEngine,
            renderingEngine,
            ObjMeshLoadingRepository(context)
        )

        cameraActivator = CameraActivator(
            isCameraPermissionGrantedSubject,
            LocalCameraRepository(renderingEngine.getDeviceCameraTextureName(), renderingEngine)
        )

        subscriptions.add(messageQueue.messages().subscribe { message ->
            when (message) {
                Message.CAMERA_PERMISSION_GRANTED -> isCameraPermissionGrantedSubject.onNext(true)
                Message.CAMERA_PERMISSION_DENIED -> isCameraPermissionGrantedSubject.onNext(false)
                Message.UI_RESUMED -> cameraActivator.onResume()
                Message.UI_PAUSED -> cameraActivator.onPause()
            }
        })

        subscriptions.add(cameraActivator.cameraInfo.subscribe { info ->
            scene.onCameraInfoUpdate(info)
        })

        subscriptions.add(
            cameraActivator.cameraInfo.subscribe {
                val camera = cameraActivator.camera ?: throw IllegalArgumentException("No camera available")
                val previewSize = camera.getSupportedPreviewSizes().maxBy { it.width } ?: throw IllegalArgumentException("No camera preview size found")
                camera.setPreviewSize(previewSize)
                scene.onCameraInfoUpdate(CameraInfo(previewSize, camera.getSensorOrientation()))
            }
        )

        return scene
    }

    override fun onDrawFrame(gl: GL10) {
        super.onDrawFrame(gl)

        cameraActivator.camera?.updatePreviewIfFrameAvailable()
    }

    override fun onCleared() {
        super.onCleared()

        subscriptions.clear()
        cameraActivator.onCleared()
    }

    enum class Message {
        CAMERA_PERMISSION_GRANTED, CAMERA_PERMISSION_DENIED, UI_RESUMED, UI_PAUSED
    }
}