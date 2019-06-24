package ilapin.earth.ui.camera

import android.content.Context
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.camera.CameraActivator
import ilapin.earth.domain.camera.CameraScene
import ilapin.earth.frameworkdependent.camera.LocalCameraRepository
import ilapin.engine3d.Scene
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(context: Context) : BaseGLSurfaceRenderer(context) {

    private val isCameraPermissionGrantedSubject = PublishSubject.create<Boolean>()
    private lateinit var cameraActivator: CameraActivator

    private var subscription: Disposable? = null

    override fun createScene(messageQueue: MessageQueue): Scene {
        val scene = CameraScene(renderingEngine, renderingEngine, renderingEngine)

        cameraActivator = CameraActivator(
            isCameraPermissionGrantedSubject,
            LocalCameraRepository("androidCameraPreviewTexture", renderingEngine)
        )

        subscription = messageQueue.messages().subscribe { message ->
            when (message) {
                is Int -> scene.applySizeModifier(message)
                Message.CAMERA_PERMISSION_GRANTED -> isCameraPermissionGrantedSubject.onNext(true)
                Message.CAMERA_PERMISSION_DENIED -> isCameraPermissionGrantedSubject.onNext(false)
                Message.UI_RESUMED -> cameraActivator.onResume()
                Message.UI_PAUSED -> cameraActivator.onPause()
            }
        }

        return scene
    }

    override fun onDrawFrame(gl: GL10) {
        super.onDrawFrame(gl)

        cameraActivator.camera?.updatePreviewIfFrameAvailable()
    }

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
        cameraActivator.onCleared()
    }

    enum class Message {
        CAMERA_PERMISSION_GRANTED, CAMERA_PERMISSION_DENIED, UI_RESUMED, UI_PAUSED
    }
}