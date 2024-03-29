package ilapin.earth.ui.compass

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.SensorManager
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.provider.Settings
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import ilapin.common.acceleration.AccelerationRepository
import ilapin.common.android.acceleration.SensorAccelerationRepository
import ilapin.common.android.magneticfield.SensorMagneticFieldRepository
import ilapin.common.android.orientation.SoftwareOrientationRepository
import ilapin.common.android.ui.MessageDialog
import ilapin.common.magneticfield.MagneticField
import ilapin.common.magneticfield.MagneticFieldRepository
import ilapin.common.orientation.OrientationRepository
import ilapin.earth.R
import ilapin.earth.domain.camera.CameraPermission
import ilapin.earth.domain.camera.CameraPermissionResolver
import ilapin.earth.frameworkdependent.camera.LocalCameraPermissionRepository
import ilapin.earth.ui.AppNavigation
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_compass.*
import kotlinx.android.synthetic.main.activity_main.containerLayout
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CompassActivity : AppCompatActivity(), MessageDialog.Listener {

    private var renderer: GLSurfaceViewRenderer? = null

    private val pausableSubscriptions = CompositeDisposable()
    private val permanentSubscriptions = CompositeDisposable()

    private val azimuthSinStatistics = DescriptiveStatistics(10)
    private val azimuthCosStatistics = DescriptiveStatistics(10)

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

        cameraPermissionResolver.resolve()

        ContextCompat.getDrawable(this, R.drawable.ic_magnet)?.let {
            DrawableCompat.setTint(it, 0xffff0000.toInt())
            magneticSensorCalibrationButton.setImageDrawable(it)
        }

        magneticSensorCalibrationButton.setOnClickListener {
            AppNavigation.showMagneticSensorCalibrationRequired(this)
        }
    }

    override fun onResume() {
        super.onResume()

        switchToImmersiveMode()

        pausableSubscriptions.add(orientationRepository.orientation().subscribe { orientation ->
            azimuthSinStatistics.addValue(sin(orientation.azimuth.toDouble()))
            azimuthCosStatistics.addValue(cos(orientation.azimuth.toDouble()))

            val azimuthDegrees = Math.toDegrees(atan2(azimuthSinStatistics.mean, azimuthCosStatistics.mean))
            val azimuthDegreesPositiveOnly = if (azimuthDegrees >= 0) {
                azimuthDegrees
            } else {
                azimuthDegrees + 360
            }
            azimuthView.text = getString(
                R.string.azimuth_degrees,
                String.format("%03d", azimuthDegreesPositiveOnly.toInt())
            )
            renderer?.putMessage(orientation)
        })

        pausableSubscriptions.add(accelerationRepository.acceleration().subscribe { acceleration ->
            renderer?.putMessage(acceleration)
        })

        pausableSubscriptions.add(magneticFieldRepository.magneticField().subscribe { magneticField ->
            magneticSensorCalibrationButton.visibility = if (magneticField.accuracy == MagneticField.Accuracy.LOW) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })

        cameraPermissionResolver.check()

        renderer?.putMessage(GLSurfaceViewRenderer.Message.UI_RESUMED)
    }

    override fun onConfirm() {
        switchToImmersiveMode()
    }

    override fun onNeutral() {
        AppNavigation.showWebPage(this, "https://www.youtube.com/results?search_query=android+compass+calibration")
        switchToImmersiveMode()
    }

    override fun onCancel() {
        switchToImmersiveMode()
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
