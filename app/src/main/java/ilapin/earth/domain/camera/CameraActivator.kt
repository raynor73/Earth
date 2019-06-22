package ilapin.earth.domain.camera

import io.reactivex.subjects.PublishSubject

class CameraActivator(private val cameraPermissionResolver: CameraPermissionResolver) {

    private val isUiActiveSubject = PublishSubject.create<Boolean>()

    fun onResume() {
        isUiActiveSubject.onNext(true)
    }

    fun onPause() {
        isUiActiveSubject.onNext(false)
    }

    fun onCleared() {

    }
}