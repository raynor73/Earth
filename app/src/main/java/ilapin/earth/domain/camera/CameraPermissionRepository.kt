package ilapin.earth.domain.camera

import io.reactivex.Single

interface CameraPermissionRepository {

    fun shouldShowRationale(): Boolean

    fun isPermissionGranted(): Boolean

    fun requestPermission(): Single<Boolean>
}