package ilapin.common.android.permissions

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import ilapin.common.permissions.Permission
import ilapin.common.permissions.PermissionRepository
import io.reactivex.Single

class AndroidPermissionsRepository(private val activity: AppCompatActivity) : PermissionRepository {

    private val permissions = RxPermissions(activity)

    override fun shouldShowRationale(permission: Permission): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, toAndroidPermission(permission))
    }

    override fun isPermissionGranted(permission: Permission): Boolean {
        return ActivityCompat
            .checkSelfPermission(activity, toAndroidPermission(permission)) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission(permission: Permission): Single<Boolean> {
        return permissions.request(toAndroidPermission(permission)).firstOrError()
    }
}