package ilapin.earth.ui.compass

import android.content.Context
import android.content.res.Configuration
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ilapin.earth.R
import ilapin.earth.domain.magneticfield.MagneticFieldRepository
import ilapin.earth.frameworkdependent.magneticfield.SensorMagneticFieldRepository
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class CompassActivity : AppCompatActivity() {

    private var renderer: GLSurfaceViewRenderer? = null

    private var subscription: Disposable? = null

    private lateinit var magneticFieldRepository: MagneticFieldRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magneticFieldRepository = SensorMagneticFieldRepository(sensorManager)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            renderer = GLSurfaceViewRenderer(this)
            val glView = GLSurfaceView(this)
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }
    }

    override fun onResume() {
        super.onResume()

        subscription = magneticFieldRepository.magneticField().subscribe { magneticField ->
            renderer?.putMessage(magneticField)
        }
    }

    override fun onPause() {
        super.onPause()
        subscription?.dispose()
    }

    override fun onDestroy() {
        super.onDestroy()
        renderer?.onCleared()
    }
}
