package ilapin.earth.domain.camera

import ilapin.common.android.log.L
import ilapin.earth.App.Companion.LOG_TAG
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

    private var _camera: Camera? = null

    val camera: Camera?
        get() = _camera

    init {
        subscription = Observable.combineLatest(
            isCameraPermissionGrantedObservable,
            isUiActiveSubject,
            BiFunction<Boolean, Boolean, State> { isCameraPermissionGranted, isUiActive -> State(isCameraPermissionGranted, isUiActive) }
        ).subscribe { state ->
            if (state.isCameraPermissionGranted && state.isUiActive) {
                L.d(LOG_TAG, "Opening camera")
                val camera = cameraRepository.openCamera()
                camera.startPreview()
                this._camera = camera
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
        _camera?.apply {
            L.d(LOG_TAG, "Releasing camera")
            stopPreview()
            onCleared()
        }
        _camera = null
    }

    private class State(val isCameraPermissionGranted: Boolean, val isUiActive: Boolean)
}