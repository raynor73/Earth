package ilapin.earth.ui.camera

import android.support.v7.app.AppCompatActivity
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.camera.CameraActivator
import ilapin.earth.domain.camera.CameraScene
import ilapin.earth.frameworkdependent.camera.LocalCameraRepository
import ilapin.engine3d.Scene
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(activity: AppCompatActivity) : BaseGLSurfaceRenderer(activity) {

    private val isCameraPermissionGrantedSubject = PublishSubject.create<Boolean>()
    private lateinit var cameraActivator: CameraActivator

    private var subscriptions = CompositeDisposable()

    override fun createScene(messageQueue: MessageQueue): Scene {
        val scene = CameraScene(renderingEngine, renderingEngine, renderingEngine)

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