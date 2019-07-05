package ilapin.common.permissions

import io.reactivex.Single

interface PermissionRepository {

    fun shouldShowRationale(permission: Permission): Boolean

    fun isPermissionGranted(permission: Permission): Boolean

    fun requestPermission(permission: Permission): Single<Boolean>
}