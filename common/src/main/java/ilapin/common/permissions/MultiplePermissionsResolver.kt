package ilapin.common.permissions

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class MultiplePermissionsResolver(
    private val permissions: List<Permission>,
    private val permissionsRepository: MultiplePermissionsRepository
) {
    private val permissionResultsSubject = BehaviorSubject.createDefault<Map<Permission, Boolean?>>(
        permissions.map { it to null }.toMap()
    )

    private var permissionsRequestSubscription: Disposable? = null

    private var isRequesting = false

    val permissionsResult: Observable<Map<Permission, Boolean?>> = permissionResultsSubject

    fun resolve() {
        if (isRequesting) {
            return
        }

        isRequesting = true

        permissionsRequestSubscription = permissionsRepository.requestPermissions(permissions).subscribe { result ->
            permissionResultsSubject.onNext(result)
            isRequesting = false
        }
    }

    fun check() {
        if (isRequesting) {
            return
        }

        permissionResultsSubject.onNext(
            permissions.map { permission -> permission to permissionsRepository.isPermissionGranted(permission) }.toMap()
        )
    }

    fun onCleared() {
        permissionsRequestSubscription?.dispose()
    }
}