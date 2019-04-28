package ilapin.earth.ui

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import ilapin.earth.domain.earthscene.EarthScene
import ilapin.earth.frameworkdependent.meshloader.ObjMeshLoadingRepository
import ilapin.earth.frameworkdependent.renderingengine.RenderingEngine
import ilapin.earth.frameworkdependent.time.LocalTimeRepository
import ilapin.engine3d.PerspectiveCameraComponent
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLSurfaceViewRenderer(
    private val context: Context
) : GLSurfaceView.Renderer {

    private var renderingEngine: RenderingEngine? = null
    private var earthScene: EarthScene? = null

    override fun onDrawFrame(gl: GL10) {
        earthScene?.update()
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
        val renderingEngine = RenderingEngine(context) { earthScene?.camera }
        this.renderingEngine = renderingEngine
        earthScene = EarthScene(
            renderingEngine,
            renderingEngine,
            renderingEngine,
            ObjMeshLoadingRepository(context),
            LocalTimeRepository()
        )
    }

    fun onCleared() {
    }
}