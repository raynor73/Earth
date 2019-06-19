package ilapin.earth.ui.camera

import android.content.Context
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.camera.CameraScene
import ilapin.engine3d.Scene
import io.reactivex.disposables.Disposable

class GLSurfaceViewRenderer(context: Context) : BaseGLSurfaceRenderer(context) {

    private var subscription: Disposable? = null

    override fun createScene(messageQueue: MessageQueue): Scene {
        val scene = CameraScene(renderingEngine, renderingEngine, renderingEngine)

        subscription = messageQueue.messages().subscribe { message ->
            when (message) {
                is Int -> scene.applySizeModifier(message)
            }
        }

        return scene
    }

    override fun onCleared() {
        super.onCleared()
        subscription?.dispose()
    }
}