package ilapin.earth.ui.compass

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.compass.CompassScene
import ilapin.earth.frameworkdependent.orientation.OrientationFromMessageQueueRepository
import ilapin.earth.frameworkdependent.renderingengine.RenderingEngine
import ilapin.engine3d.PerspectiveCameraComponent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private val messageQueue = MessageQueue()

    private var scene: CompassScene? = null
    private var renderingEngine: RenderingEngine? = null

    override fun onDrawFrame(gl: GL10) {
        messageQueue.update()
        renderingEngine?.render()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        renderingEngine?.updateCameraConfig(PerspectiveCameraComponent.Config(
            45f,
            width.toFloat() / height.toFloat(),
            0.1f,
            1000f
        ))
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        val renderingEngine = RenderingEngine(context) { scene?.camera }
        this.renderingEngine = renderingEngine
        scene = CompassScene(
            renderingEngine,
            OrientationFromMessageQueueRepository(messageQueue),
            renderingEngine,
            renderingEngine
        )
    }

    fun putMessage(message: Any) {
        messageQueue.putMessage(message)
    }

    fun onCleared() {
        scene?.onCleared()
    }
}