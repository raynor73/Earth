package ilapin.earth.domain.camera

import io.reactivex.Single

interface CameraRepository {

    fun openCamera(): Single<Camera>
}