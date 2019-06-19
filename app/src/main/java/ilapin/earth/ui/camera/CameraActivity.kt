package ilapin.earth.ui.camera

import android.Manifest
import android.content.res.Configuration
import android.opengl.GLSurfaceView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.tbruyelle.rxpermissions2.RxPermissions
import ilapin.earth.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {

    private val subscriptions = CompositeDisposable()

    private var renderer: GLSurfaceViewRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        val permissions = RxPermissions(this)
        subscriptions.add(permissions.requestEach(Manifest.permission.CAMERA)
            .subscribe { permission ->
                if (permission.granted) {

                } else if (permission.shouldShowRequestPermissionRationale) {

                } else {

                }
            }
        )

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
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
        subscriptions.clear()
        renderer?.onCleared()
    }
}
