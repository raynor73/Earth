package ilapin.common.android.permissions

import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import ilapin.common.permissions.MultiplePermissionsRepository
import ilapin.common.permissions.Permission
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import com.tbruyelle.rxpermissions2.Permission as RxPermission

class AndroidMultiplePermissionsRepository(private val activity: AppCompatActivity) : MultiplePermissionsRepository {

    private val rxPermissions = RxPermissions(activity)

    override fun shouldShowRationale(permission: Permission): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, toAndroidPermission(permission))
    }

    override fun isPermissionGranted(permission: Permission): Boolean {
        return ContextCompat.checkSelfPermission(activity, toAndroidPermission(permission)) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermissions(permissions: List<Permission>): Single<Map<Permission, Boolean>> {
        return rxPermissions
            .requestEach(*permissions.map { toAndroidPermission(it) }.toTypedArray())
            .zipWith(Observable.fromIterable(permissions), BiFunction<RxPermission, Permission, Pair<Permission, RxPermission>> { permissionResult, permission -> Pair(permission, permissionResult) })
            .toList()
            .flatMap { result -> Single.just(result.map { it.first to it.second.granted }.toMap()) }
    }
}