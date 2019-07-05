package ilapin.earth.ui.celestial_sphere

import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import ilapin.common.input.TouchEvent
import ilapin.earth.R
import kotlinx.android.synthetic.main.activity_main.*

class CelestialSphereActivity : AppCompatActivity() {

    private var renderer: GLSurfaceViewRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_celestial_sphere)

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            renderer = GLSurfaceViewRenderer(this)
            val glView = GLSurfaceView(this)
            glView.setOnTouchListener { _, event ->
                renderer?.putMessage(
                    TouchEvent(
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> TouchEvent.Action.DOWN
                            MotionEvent.ACTION_MOVE -> TouchEvent.Action.MOVE
                            MotionEvent.ACTION_UP -> TouchEvent.Action.UP
                            else -> TouchEvent.Action.CANCEL
                        },
                        event.x.toInt(),
                        event.y.toInt()
                    )
                )
                true
            }
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }
    }

    override fun onResume() {
        super.onResume()

        switchToImmersiveMode()
    }

    private fun switchToImmersiveMode() {
        containerLayout.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
}
