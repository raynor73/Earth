package ilapin.common.permissions

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class PermissionResolver(private val permissionRepository: PermissionRepository, private val permission: Permission) {

    private val permissionStateSubject = BehaviorSubject.createDefault(PermissionRequestState.UNKNOWN)

    private var permissionRequestSubscription: Disposable? = null

    val permissionState: Observable<PermissionRequestState> = permissionStateSubject

    fun resolve() {
        if (permissionStateSubject.value == PermissionRequestState.REQUESTING) {
            return
        }

        permissionStateSubject.onNext(PermissionRequestState.REQUESTING)

        if (permissionRepository.isPermissionGranted(permission)) {
            permissionStateSubject.onNext(PermissionRequestState.GRANTED)
        } else {
            permissionRequestSubscription = permissionRepository.requestPermission(permission).subscribe { isGranted ->
                permissionStateSubject.onNext(if (isGranted) PermissionRequestState.GRANTED else PermissionRequestState.DENIED)
            }
        }
    }

    fun check() {
        if (permissionStateSubject.value == PermissionRequestState.REQUESTING) {
            return
        }

        permissionStateSubject.onNext(
            if (permissionRepository.isPermissionGranted(permission)) PermissionRequestState.GRANTED else PermissionRequestState.DENIED
        )
    }

    fun shouldShowRationale() = permissionRepository.shouldShowRationale(permission)

    fun onCleared() {
        permissionRequestSubscription?.dispose()
    }
}