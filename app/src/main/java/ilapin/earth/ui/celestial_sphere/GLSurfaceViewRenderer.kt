package ilapin.earth.ui.celestial_sphere

import android.content.Context
import ilapin.common.android.input.TouchEventFromMessageQueueRepository
import ilapin.common.android.meshloader.ObjMeshLoadingRepository
import ilapin.common.android.renderingengine.AndroidDisplayMetricsRepository
import ilapin.common.android.renderingengine.BaseGLSurfaceRenderer
import ilapin.common.messagequeue.MessageQueue
import ilapin.engine3d.Scene

class GLSurfaceViewRenderer(private val context: Context) : BaseGLSurfaceRenderer(context) {

    override fun createScene(messageQueue: MessageQueue): Scene {
        return CelestialSphereScene(
            renderingEngine,
            renderingEngine,
            renderingEngine,
            renderingEngine,
            ObjMeshLoadingRepository(context),
            TouchEventFromMessageQueueRepository(messageQueue),
            AndroidDisplayMetricsRepository(context)
        )
    }
}