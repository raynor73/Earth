package ilapin.earth.domain.celestial_sphere

import ilapin.common.android.orientation.TrueNorthOrientationRepository
import ilapin.common.location.Location
import ilapin.common.location.LocationsRepository
import ilapin.common.math.RotationMatrixSmoother
import ilapin.common.orientation.OrientationRepository
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import org.joml.*

class CelestialSphere(
    orientationRepository: OrientationRepository,
    locationsRepository: LocationsRepository
) {

    private val modelRotationSubject = BehaviorSubject.createDefault<Quaternionfc>(Quaternionf().identity())

    //private var lastLocation: Location? = null

    private val rotationMatrixSmoother = RotationMatrixSmoother(10)
    private val trueNorthOrientationRepository = TrueNorthOrientationRepository(
        orientationRepository,
        locationsRepository
    )

    private val subscriptions = CompositeDisposable()

    private val tmpMatrix = Matrix4f()
    private val tmpQuaternion = Quaternionf()

    val modelRotation: Observable<Quaternionfc> = modelRotationSubject

    init {
        trueNorthOrientationRepository.orientation().map { it.rotationMatrix }.subscribe(rotationMatrixSmoother)

        subscriptions.add(
            Observable.combineLatest(
                rotationMatrixSmoother.smoothedRotationMatrix,
                locationsRepository.locations(),
                BiFunction<Matrix4fc, Location, Quaternionf> { orientationMatrix, location ->
                    tmpMatrix.set(orientationMatrix).invert()
                    tmpQuaternion.setFromUnnormalized(tmpMatrix)
                    tmpQuaternion.rotateLocalX(Math.toRadians(location.latitude).toFloat())
                    tmpQuaternion
                }
            ).subscribe { rotation ->
                modelRotationSubject.onNext(Quaternionf(rotation))
            }
        )
    }

    /*fun convert(celestialLocation: CelestialLocation): Vector3f? {
        return lastLocation?.let {
            Vector3f(1f, 0f, 0f)
        }
    }*/

    fun onCleared() {
        subscriptions.clear()
        rotationMatrixSmoother.dispose()
    }
}