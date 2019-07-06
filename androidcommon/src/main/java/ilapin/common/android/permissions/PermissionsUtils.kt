package ilapin.common.android.permissions

import android.Manifest
import ilapin.common.permissions.Permission

fun toAndroidPermission(permission: Permission): String {
    return when (permission) {
        Permission.CAMERA -> Manifest.permission.CAMERA
        Permission.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
    }
}
