package ilapin.earth.ui.camera

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import ilapin.earth.R
import ilapin.earth.domain.camera.CameraPermission
import ilapin.earth.domain.camera.CameraPermissionResolver
import ilapin.earth.frameworkdependent.camera.LocalCameraPermissionRepository
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    private val subscriptions = CompositeDisposable()

    private var renderer: GLSurfaceViewRenderer? = null

    private lateinit var cameraPermissionResolver: CameraPermissionResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        cameraPermissionResolver = CameraPermissionResolver(LocalCameraPermissionRepository(this))

        subscriptions.add(cameraPermissionResolver.permission.subscribe { permission ->
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

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            renderer = GLSurfaceViewRenderer(this)
            val glView = GLSurfaceView(this)
            glView.setEGLContextClientVersion(2)
            glView.setRenderer(renderer)
            glView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
            containerLayout.addView(glView, 0)
        }

        cameraPermissionResolver.resolve()

        previewSizeSeekBar.max = 100
        previewSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                renderer?.putMessage(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // do nothing
            }
        })
    }

    override fun onResume() {
        super.onResume()

        cameraPermissionResolver.check()

        renderer?.putMessage(GLSurfaceViewRenderer.Message.UI_RESUMED)
    }

    override fun onPause() {
        super.onPause()

        renderer?.putMessage(GLSurfaceViewRenderer.Message.UI_PAUSED)
    }

    override fun onDestroy() {
        super.onDestroy()

        subscriptions.clear()
        renderer?.onCleared()
        cameraPermissionResolver.onCleared()
    }
}
