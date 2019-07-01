package ilapin.earth.ui.compass

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.SensorManager
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import ilapin.earth.R
import ilapin.common.acceleration.AccelerationRepository
import ilapin.common.magneticfield.MagneticFieldRepository
import ilapin.common.orientation.OrientationRepository
import ilapin.common.android.acceleration.SensorAccelerationRepository
import ilapin.common.android.magneticfield.SensorMagneticFieldRepository
import ilapin.common.android.orientation.SoftwareOrientationRepository
import ilapin.earth.domain.camera.CameraPermission
import ilapin.earth.domain.camera.CameraPermissionResolver
import ilapin.earth.frameworkdependent.camera.LocalCameraPermissionRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_compass.*
import kotlinx.android.synthetic.main.activity_compass.enableCameraButton
import kotlinx.android.synthetic.main.activity_compass.gotoPermissionSettingsLayout
import kotlinx.android.synthetic.main.activity_main.containerLayout

class CompassActivity : AppCompatActivity() {

    private var renderer: GLSurfaceViewRenderer? = null

    private val pausableSubscriptions = CompositeDisposable()
    private val permanentSubscriptions = CompositeDisposable()

    private lateinit var magneticFieldRepository: MagneticFieldRepository
    private lateinit var accelerationRepository: AccelerationRepository
    private lateinit var orientationRepository: OrientationRepository

    private lateinit var cameraPermissionResolver: CameraPermissionResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        cameraPermissionResolver = CameraPermissionResolver(LocalCameraPermissionRepository(this))

        permanentSubscriptions.add(cameraPermissionResolver.permission.subscribe { permission ->
            when (permission) {
                CameraPermission.GRANTED -> {
                    renderer?.putMessage(GLSurfaceViewRenderer.Message.CAMERA_PERMISSION_GRANTED)

                    enableCameraButton.visibility = View.GONE
                    gotoPermissionSettingsLayout.visibility = View.GONE
                }
                CameraPermission.DENIED -> {
                    renderer?.putMessage(GLSurfaceViewRenderer.Message.CAMERA_PERMISSION_DENIED)

                    if (cameraPermissionResolver.shouldShowRationale()) {
                        enableCameraButton.visibility = View.VISIBLE
                        gotoPermissionSettingsLayout.visibility = View.GONE
                    } else {
                        enableCameraButton.visibility = View.GONE
                        gotoPermissionSettingsLayout.visibility = View.VISIBLE
                    }
                }
                else -> {
                    enableCameraButton.visibility = View.GONE
                    gotoPermissionSettingsLayout.visibility = View.GONE
                }
            }
        })

        enableCameraButton.setOnClickListener { cameraPermissionResolver.resolve() }
        gotoPermissionSettingsButton.setOnClickListener {
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
                startActivity(this)
            }
        }

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

        cameraPermissionResolver.resolve()
    }

    override fun onResume() {
        super.onResume()

        pausableSubscriptions.add(magneticFieldRepository.magneticField().subscribe { magneticField ->
            magneticFieldView.text = getString(
                R.string.magnetic_field_values,
                magneticField.x.toString(),
                magneticField.y.toString(),
                magneticField.z.toString()
            )
        })
        pausableSubscriptions.add(orientationRepository.orientation().subscribe { orientation ->
            renderer?.putMessage(orientation)
        })

        cameraPermissionResolver.check()

        renderer?.putMessage(GLSurfaceViewRenderer.Message.UI_RESUMED)
    }

    override fun onPause() {
        super.onPause()

        pausableSubscriptions.clear()

        renderer?.putMessageAndWaitForExecution(GLSurfaceViewRenderer.Message.UI_PAUSED)
    }

    override fun onDestroy() {
        super.onDestroy()

        renderer?.onCleared()
        permanentSubscriptions.clear()
        cameraPermissionResolver.onCleared()
    }
}
