package ilapin.earth.ui.compass

import android.content.Context
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.messagequeue.MessageQueue
import ilapin.common.orientation.OrientationFromMessageQueueRepository
import ilapin.earth.domain.compass.CompassScene
import ilapin.engine3d.Scene

class GLSurfaceViewRenderer(context: Context) : BaseGLSurfaceRenderer(context) {

    override fun createScene(messageQueue: MessageQueue): Scene {
        return CompassScene(
            renderingEngine,
            OrientationFromMessageQueueRepository(messageQueue),
            renderingEngine,
            renderingEngine
        )
    }
}