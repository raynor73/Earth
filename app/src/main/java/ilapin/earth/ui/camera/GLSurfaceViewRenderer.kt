package ilapin.earth.ui.camera

import android.content.Context
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.camera.CameraScene
import ilapin.engine3d.Scene

class GLSurfaceViewRenderer(context: Context) : BaseGLSurfaceRenderer(context) {

    override fun createScene(messageQueue: MessageQueue): Scene {
        return CameraScene(renderingEngine, renderingEngine, renderingEngine)
    }
}