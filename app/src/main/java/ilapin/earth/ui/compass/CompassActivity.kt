package ilapin.earth.ui.compass

import android.content.Context
import android.content.res.Configuration
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ilapin.earth.R
import ilapin.earth.domain.acceleration.AccelerationRepository
import ilapin.earth.domain.magneticfield.MagneticFieldRepository
import ilapin.earth.domain.orientation.OrientationRepository
import ilapin.earth.frameworkdependent.acceleration.SensorAccelerationRepository
import ilapin.earth.frameworkdependent.magneticfield.SensorMagneticFieldRepository
import ilapin.earth.frameworkdependent.orientation.SoftwareOrientationRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_compass.*
import kotlinx.android.synthetic.main.activity_main.containerLayout

class CompassActivity : AppCompatActivity() {

    private var renderer: GLSurfaceViewRenderer? = null

    private val subscriptions = CompositeDisposable()

    private lateinit var magneticFieldRepository: MagneticFieldRepository
    private lateinit var accelerationRepository: AccelerationRepository
    private lateinit var orientationRepository: OrientationRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        magneticFieldRepository = SensorMagneticFieldRepository(sensorManager)
        accelerationRepository = SensorAccelerationRepository(sensorManager)
        orientationRepository = SoftwareOrientationRepository(
            accelerationRepository,
            magneticFieldRepository
        )

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            renderer = GLSurfaceViewRenderer(this)
            val glView = GLSurfaceView(this)
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }

        val notAvailableString = getString(R.string.n_a)
        magneticFieldView.text = getString(
            R.string.magnetic_field_values,
            notAvailableString,
            notAvailableString,
            notAvailableString
        )
    }

    override fun onResume() {
        super.onResume()

        subscriptions.add(magneticFieldRepository.magneticField().subscribe { magneticField ->
            magneticFieldView.text = getString(
                R.string.magnetic_field_values,
                magneticField.x.toString(),
                magneticField.y.toString(),
                magneticField.z.toString()
            )
        })
        subscriptions.add(orientationRepository.orientation().subscribe { orientation ->
            renderer?.putMessage(orientation)
        })
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        renderer?.onCleared()
    }
}
