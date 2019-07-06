package ilapin.common.permissions

import io.reactivex.Single

interface MultiplePermissionsRepository {

    fun shouldShowRationale(permission: Permission): Boolean

    fun isPermissionGranted(permission: Permission): Boolean

    fun requestPermissions(permissions: List<Permission>): Single<Map<Permission, Boolean>>
}