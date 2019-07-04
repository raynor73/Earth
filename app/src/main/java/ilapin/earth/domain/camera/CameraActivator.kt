package ilapin.earth.domain.camera

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class CameraActivator(
    isCameraPermissionGrantedObservable: Observable<Boolean>,
    private val cameraRepository: CameraRepository
) {
    private val isUiActiveSubject = PublishSubject.create<Boolean>()
    private val cameraInfoSubject = BehaviorSubject.create<CameraInfo>()

    private var subscription: Disposable? = null

    private var _camera: Camera? = null

    val cameraInfo: Observable<CameraInfo> = cameraInfoSubject

    val camera: Camera?
        get() = _camera

    init {
        subscription = Observable.combineLatest(
            isCameraPermissionGrantedObservable,
            isUiActiveSubject,
            BiFunction<Boolean, Boolean, State> { isCameraPermissionGranted, isUiActive -> State(isCameraPermissionGranted, isUiActive) }
        ).subscribe { state ->
            if (state.isCameraPermissionGranted && state.isUiActive) {
                val camera = cameraRepository.openCamera()
                _camera = camera
                cameraInfoSubject.onNext(CameraInfo(camera.getPreviewSize(), camera.getSensorOrientation()))
                camera.startPreview()
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
            stopPreview()
            onCleared()
        }
        _camera = null
    }

    private class State(val isCameraPermissionGranted: Boolean, val isUiActive: Boolean)
}