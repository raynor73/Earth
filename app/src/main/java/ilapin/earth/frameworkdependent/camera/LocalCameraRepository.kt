package ilapin.earth.frameworkdependent.camera

import ilapin.earth.domain.camera.Camera
import ilapin.earth.domain.camera.CameraRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.hardware.Camera as AndroidCamera

class LocalCameraRepository : CameraRepository {

    override fun openCamera(): Single<Camera> {
        return Single.create<Camera> { emitter ->
            emitter.onSuccess(LocalCamera(AndroidCamera.open()))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}