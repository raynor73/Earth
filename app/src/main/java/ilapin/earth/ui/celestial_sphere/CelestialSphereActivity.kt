package ilapin.earth.ui.celestial_sphere

import android.content.Context
import android.content.res.Configuration
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import ilapin.common.android.acceleration.SensorAccelerationRepository
import ilapin.common.android.location.AndroidLocationsRepository
import ilapin.common.android.magneticfield.SensorMagneticFieldRepository
import ilapin.common.android.orientation.SoftwareOrientationRepository
import ilapin.common.android.permissions.AndroidMultiplePermissionsRepository
import ilapin.common.android.ui.RxLifecycleEventsActivity
import ilapin.common.input.TouchEvent
import ilapin.common.location.LocationsRepository
import ilapin.common.orientation.OrientationRepository
import ilapin.common.permissions.MultiplePermissionsResolver
import ilapin.common.permissions.Permission
import ilapin.earth.R
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.activity_celestial_sphere.*
import kotlinx.android.synthetic.main.activity_main.containerLayout

class CelestialSphereActivity : RxLifecycleEventsActivity() {

    private var renderer: GLSurfaceViewRenderer? = null

    private lateinit var orientationRepository: OrientationRepository
    private lateinit var locationsRepository: LocationsRepository

    private val permissionsResolver = MultiplePermissionsResolver(
        listOf(Permission.CAMERA, Permission.LOCATION),
        AndroidMultiplePermissionsRepository(this)
    )

    private val permanentSubscriptions = CompositeDisposable()
    private val pausableSubscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_celestial_sphere)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
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

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        orientationRepository = SoftwareOrientationRepository(
            SensorAccelerationRepository(sensorManager),
            SensorMagneticFieldRepository(sensorManager)
        )

        locationsRepository = AndroidLocationsRepository(this)

        permanentSubscriptions.add(permissionsResolver.permissionsResult.subscribe { permissionsResult ->
            permissionsResult.forEach { (permission, status) ->
                when (permission) {
                    Permission.CAMERA -> cameraPermissionView.text = getString(
                        R.string.camera_permission_status,
                        when (status) {
                            true -> getString(R.string.permission_status_granted)
                            false -> getString(R.string.permission_status_denied)
                            else -> getString(R.string.permission_status_unknown)
                        }
                    )
                    Permission.LOCATION -> locationPermissionView.text = getString(
                        R.string.location_permission_status,
                        when (status) {
                            true -> getString(R.string.permission_status_granted)
                            false -> getString(R.string.permission_status_denied)
                            else -> getString(R.string.permission_status_unknown)
                        }
                    )
                }
            }
        })

        permanentSubscriptions.add(
            Observable.combineLatest(
                lifecycleEvents,
                permissionsResolver.permissionsResult,
                BiFunction<Event, Map<Permission, Boolean?>, Unit> { lifecycleEvent, permissions ->
                    if (lifecycleEvent == Event.ON_RESUME) {
                        pausableSubscriptions.add(orientationRepository.orientation().subscribe { orientation ->
                            renderer?.putMessage(orientation)
                        })
                        if (permissions[Permission.LOCATION] == true) {
                            pausableSubscriptions.add(locationsRepository.locations().subscribe { location ->
                                renderer?.putMessage(location)
                            })
                        }
                    } else {
                        pausableSubscriptions.clear()
                    }
                }
            ).subscribe()
        )

        permissionsResolver.resolve()
    }

    override fun onResume() {
        super.onResume()

        switchToImmersiveMode()

        permissionsResolver.check()
    }

    override fun onDestroy() {
        super.onDestroy()

        permanentSubscriptions.clear()
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
