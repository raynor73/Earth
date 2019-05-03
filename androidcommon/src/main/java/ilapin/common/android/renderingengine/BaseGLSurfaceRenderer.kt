package ilapin.common.android.renderingengine

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.common.messagequeue.MessageQueue
import ilapin.engine3d.PerspectiveCameraComponent
import ilapin.engine3d.Scene
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class BaseGLSurfaceRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    protected val messageQueue = MessageQueue()

    private var scene: Scene? = null
    protected lateinit var renderingEngine: RenderingEngine

    override fun onDrawFrame(gl: GL10) {
        messageQueue.update()
        scene?.update()
        renderingEngine.render()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        renderingEngine.updateCameraConfig(PerspectiveCameraComponent.Config(
            45f,
            width.toFloat() / height.toFloat(),
            0.1f,
            1000f
        ))
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig) {
        val renderingEngine = RenderingEngine(context) { scene?.camera }
        this.renderingEngine = renderingEngine
        scene = createScene(messageQueue)
    }

    abstract fun createScene(messageQueue: MessageQueue): Scene

    fun putMessage(message: Any) {
        messageQueue.putMessage(message)
    }

    open fun onCleared() {
        scene?.onCleared()
    }
}