package ilapin.earth.domain.camera

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class CameraActivator(
    private val cameraPermissionResolver: CameraPermissionResolver,
    private val cameraRepository: CameraRepository
) {

    private val isUiActiveSubject = PublishSubject.create<Boolean>()

    private var subscription: Disposable? = null

    private var camera: Camera? = null

    init {
        subscription = Observable.combineLatest(
            cameraPermissionResolver.permission,
            isUiActiveSubject
        ) { permission, isUiActive -> State(permission, isUiActive) }.subscribe { state ->
            if (state.permission == CameraPermission.GRANTED && state.isUiActive) {
                val camera = cameraRepository.openCamera()
                camera.startPreview()
                this.camera = camera
            } else {
                releaseCamera()
            }
        }
    }

    fun onResume() {
        isUiActiveSubject.onNext(true)
    }

    fun onPause() {
        isUiActiveSubject.onNext(false)
    }

    fun onCleared() {
        subscription?.dispose()
        releaseCamera()
    }

    private fun releaseCamera() {
        camera?.stopPreview()
        camera?.release()
        camera = null
    }

    private class State(val permission: CameraPermission, val isUiActive: Boolean)
}