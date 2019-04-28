package ilapin.earth.ui

import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ilapin.earth.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var renderer: GLSurfaceViewRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            renderer = GLSurfaceViewRenderer(this)
            val glView = GLSurfaceView(this)
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        renderer?.onCleared()
    }
}
