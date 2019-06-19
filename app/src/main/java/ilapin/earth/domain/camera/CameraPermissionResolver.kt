package ilapin.earth.domain.camera

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class CameraPermissionResolver(private val permissionRepository: CameraPermissionRepository) {

    private val permissionSubject = BehaviorSubject.createDefault(CameraPermission.UNKNOWN)

    private var permissionRequestSubscription: Disposable? = null

    val permission: Observable<CameraPermission> = permissionSubject

    fun resolve() {
        if (permissionSubject.value == CameraPermission.REQUESTING) {
            return
        }

        permissionSubject.onNext(CameraPermission.REQUESTING)

        if (permissionRepository.isPermissionGranted()) {
            permissionSubject.onNext(CameraPermission.GRANTED)
        } else {
            permissionRequestSubscription = permissionRepository.requestPermission().subscribe { isGranted ->
                permissionSubject.onNext(if (isGranted) CameraPermission.GRANTED else CameraPermission.DENIED)
            }
        }
    }

    fun check() {
        if (permissionSubject.value == CameraPermission.REQUESTING) {
            return
        }

        permissionSubject.onNext(
            if (permissionRepository.isPermissionGranted()) CameraPermission.GRANTED else CameraPermission.DENIED
        )
    }

    fun shouldShowRationale() = permissionRepository.shouldShowRationale()

    fun onCleared() {
        permissionRequestSubscription?.dispose()
    }
}