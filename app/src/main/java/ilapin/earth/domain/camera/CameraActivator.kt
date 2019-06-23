package ilapin.earth.domain.camera

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

class CameraActivator(
    isCameraPermissionGrantedObservable: Observable<Boolean>,
    private val cameraRepository: CameraRepository
) {

    private val isUiActiveSubject = PublishSubject.create<Boolean>()

    private var subscription: Disposable? = null

    private var camera: Camera? = null

    init {
        subscription = Observable.combineLatest(
            isCameraPermissionGrantedObservable,
            isUiActiveSubject,
            BiFunction<Boolean, Boolean, State> { isCameraPermissionGranted, isUiActive -> State(isCameraPermissionGranted, isUiActive) }
        ).subscribe { state ->
            if (state.isCameraPermissionGranted && state.isUiActive) {
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
        camera?.onCleared()
        camera = null
    }

    private class State(val isCameraPermissionGranted: Boolean, val isUiActive: Boolean)
}