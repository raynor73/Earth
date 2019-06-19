package ilapin.earth.frameworkdependent.camera

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import ilapin.earth.domain.camera.CameraPermissionRepository
import io.reactivex.Single

class LocalCameraPermissionRepository(private val activity: AppCompatActivity) : CameraPermissionRepository {

    private val permissions = RxPermissions(activity)

    override fun shouldShowRationale() = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)

    override fun isPermissionGranted() = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    override fun requestPermission(): Single<Boolean> {
        return permissions.request(Manifest.permission.CAMERA).firstOrError()
    }
}