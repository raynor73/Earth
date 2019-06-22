package ilapin.earth.ui.camera

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import ilapin.earth.R
import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraPermission
import ilapin.earth.domain.camera.CameraPermissionResolver
import ilapin.earth.domain.camera.CameraRepository
import ilapin.earth.frameworkdependent.camera.LocalCameraPermissionRepository
import ilapin.earth.frameworkdependent.camera.LocalCameraRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    private val subscriptions = CompositeDisposable()

    private var renderer: GLSurfaceViewRenderer? = null

    private lateinit var cameraPermissionResolver: CameraPermissionResolver
    private lateinit var cameraRepository: CameraRepository

    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraPermissionResolver = CameraPermissionResolver(LocalCameraPermissionRepository(this))
        cameraRepository = LocalCameraRepository()

        subscriptions.add(cameraPermissionResolver.permission.subscribe { permission ->
            when (permission) {
                CameraPermission.GRANTED -> {
                    enableCameraButton.visibility = View.GONE
                    gotoPermissionSettingsLayout.visibility = View.GONE

                    cameraRepository.openCamera()
                }
                CameraPermission.DENIED -> {
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

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            renderer = GLSurfaceViewRenderer(this)
            val glView = GLSurfaceView(this)
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }

        cameraPermissionResolver.resolve()
    }

    override fun onResume() {
        super.onResume()

        cameraPermissionResolver.check()
    }

    

    override fun onDestroy() {
        super.onDestroy()

        subscriptions.clear()
        renderer?.onCleared()
        cameraPermissionResolver.onCleared()
    }
}
