package ilapin.earth.ui

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.common.messagequeue.MessageQueue
import ilapin.earth.domain.terrainscene.GenerateMapMessage
import ilapin.earth.domain.terrainscene.TerrainScene
import ilapin.earth.frameworkdependent.renderingengine.RenderingEngine
import ilapin.earth.frameworkdependent.time.LocalTimeRepository
import ilapin.engine3d.PerspectiveCameraComponent
import io.reactivex.disposables.Disposable
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private val messageQueue = MessageQueue()
    private var subscription: Disposable? = null

    private var renderingEngine: RenderingEngine? = null
    private var scene: TerrainScene? = null

    override fun onDrawFrame(gl: GL10) {
        messageQueue.update()
        scene?.update()
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
        scene = TerrainScene(
            renderingEngine,
            renderingEngine,
            renderingEngine,
            LocalTimeRepository()
        )
        subscription = messageQueue.messages().subscribe { message ->
            when (message) {
                is GenerateMapMessage -> {
                    scene?.mapGenerator?.also {
                        it.drawMode = message.drawMode
                        it.mapWidth = message.mapWidth
                        it.mapHeight = message.mapHeight
                        it.seed = message.seed
                        it.noiseScale = message.noiseScale
                        it.octaves = message.octaves
                        it.persistence = message.persistence
                        it.lacunarity = message.lacunarity
                        it.offset.set(message.offset)
                        it.generateMap()
                    }
                }
            }
        }
    }

    fun putMessage(message: Any) {
        messageQueue.putMessage(message)
    }

    fun onCleared() {
    }
}